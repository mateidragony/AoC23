#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdbool.h>

#define unsafe_op(e) {fprintf(stdout, "Error with %s\n", e); exit(EXIT_FAILURE);}

#define SPLIT_BUFF 10
#define BUFFER 1024

#define UP 0    // 0001
#define DOWN 1  // 0010
#define LEFT 2  // 0100
#define RIGHT 3 // 1000

typedef struct{
    char c;
    char directions_allowed;
} grid_space;

typedef struct{
    int row;
    int col;
    char dir;
} end_grid_space;


void set_char_arr_vals(char *arr, char val, int len){
    for(int i=0; i<len; ++i) arr[i] = val;
}

char *read_file(const char *file_name, int *buf_sz){
    int fd;
    if((fd = open(file_name, O_RDONLY)) == -1) unsafe_op("open");

    struct stat sb;
    if(fstat(fd, &sb) == -1) unsafe_op("fstat");
    *buf_sz = (intmax_t)sb.st_size;

    char *buf; if((buf = malloc(*buf_sz)) == NULL) unsafe_op("malloc");
    read(fd, buf, *buf_sz);

    return buf;
}

char **get_lines(char *buf, int buf_sz, int *num_lines){
    int line_buf_sz = BUFFER;
    char **lines; if((lines = malloc(BUFFER * sizeof(char*))) == NULL) unsafe_op("malloc");
    *num_lines = 0;

    int cur_wrd_idx = 0;
    int cur_wrd_buf_sz = BUFFER;
    char *cur_wrd; if((cur_wrd = malloc(BUFFER * sizeof(char))) == NULL) unsafe_op("malloc");

    for(int i=0; i<buf_sz; ++i){
        if(buf[i] != '\n'){ // No new line
            cur_wrd[cur_wrd_idx++] = buf[i];
            if(cur_wrd_idx >= cur_wrd_buf_sz){ // prolly wont happen :P
                cur_wrd_buf_sz += BUFFER;
                if((cur_wrd = realloc(cur_wrd, cur_wrd_buf_sz * sizeof(char))) == NULL) unsafe_op("realloc");
            }
        } else { // new word
            cur_wrd[cur_wrd_idx] = '\0';
            lines[(*num_lines)++] = cur_wrd;

            if(*num_lines >= line_buf_sz){
                line_buf_sz += BUFFER;
                if((lines = realloc(lines, line_buf_sz * sizeof(char*))) == NULL) unsafe_op("realloc");
            }

            cur_wrd_idx = 0;
            if((cur_wrd = malloc(BUFFER * sizeof(char))) == NULL) unsafe_op("malloc");
        }
    }

    cur_wrd[cur_wrd_idx] = '\0';
    lines[(*num_lines)++] = cur_wrd;

    free(buf); 
    buf = NULL;

    return lines;
}

char **split(char *str, const char *split_str, int *num_splits){
    char **splits; if((splits = malloc(SPLIT_BUFF * sizeof(char*))) == NULL) unsafe_op("malloc");
    const int str_len = strlen(str);
    const int split_len = strlen(split_str);

    int splits_buf_sz = SPLIT_BUFF;
    *num_splits = 0;

    int cur_wrd_idx = 0;
    int cur_wrd_buf_sz = BUFFER;
    char *cur_wrd; if((cur_wrd = malloc(BUFFER * sizeof(char))) == NULL) unsafe_op("malloc");

    int cur_split_idx = 0;
    char split_buf[split_len];
    set_char_arr_vals(split_buf, '\0', split_len);

    for(int i=0; i<str_len; ++i){
        if(str[i] != split_str[cur_split_idx]){ // Not in split str
            cur_split_idx = 0;

            int split_buf_str_len = strlen(split_buf);
            if(split_buf_str_len > 0){ // If I was in a failed split and I haven't added my split buff yet
                
                for(int j=0; j<split_buf_str_len; ++j){
                    cur_wrd[cur_wrd_idx++] = split_buf[j];
                    if(cur_wrd_idx >= cur_wrd_buf_sz){ // prolly wont happen :P (realloc)
                        cur_wrd_buf_sz += BUFFER;
                        if((cur_wrd = realloc(cur_wrd, cur_wrd_buf_sz * sizeof(char))) == NULL) unsafe_op("realloc");
                    }
                }
            }

            cur_wrd[cur_wrd_idx++] = str[i];
            if(cur_wrd_idx >= cur_wrd_buf_sz){ // prolly wont happen :P
                cur_wrd_buf_sz += BUFFER;
                if((cur_wrd = realloc(cur_wrd, cur_wrd_buf_sz * sizeof(char))) == NULL) unsafe_op("realloc");
            }
        } else {
            split_buf[cur_split_idx] = split_str[cur_split_idx];
            cur_split_idx++;
            
            if((cur_split_idx) == split_len){
                set_char_arr_vals(split_buf, '\0', split_len); // We found a split so reset split_buf to 0
                
                cur_wrd[cur_wrd_idx] = '\0';
                if(strlen(cur_wrd) > 0) splits[(*num_splits)++] = cur_wrd;

                if(*num_splits >= splits_buf_sz){
                    splits_buf_sz += SPLIT_BUFF;
                    if((splits = realloc(splits, splits_buf_sz * sizeof(char*))) == NULL) unsafe_op("realloc");
                }

                cur_wrd_idx = 0;
                if((cur_wrd = malloc(BUFFER * sizeof(char))) == NULL) unsafe_op("malloc");

                cur_split_idx = 0;
            }
        }
    }   


    int split_buf_str_len = strlen(split_buf);
    if(split_buf_str_len > 0){ // If I was in a failed split and I haven't added my split buff yet
        
        for(int j=0; j<split_buf_str_len; ++j){
            cur_wrd[cur_wrd_idx++] = split_buf[j];
            if(cur_wrd_idx >= cur_wrd_buf_sz){ // prolly wont happen :P (realloc)
                cur_wrd_buf_sz += BUFFER;
                if((cur_wrd = realloc(cur_wrd, cur_wrd_buf_sz * sizeof(char))) == NULL) unsafe_op("realloc");
            }
        }
    } 

    cur_wrd[cur_wrd_idx] = '\0';
    if(strlen(cur_wrd) > 0) splits[(*num_splits)++] = cur_wrd;

    return splits;
}

void move_light(grid_space **grid, int row, int col, int rows, int cols, char dir){

    // printf("Move light - row: %d, col; %d\n", row, col);

    if(row < 0 || col < 0 || row >= rows || col >= cols) return; // Out of bounds

    // I haven't checked my current position, so whatever char is there is what I am doing
    char cur_char = grid[row][col].c;
    char cur_dirs = grid[row][col].directions_allowed;

    if(cur_dirs >> dir & 1) return; // We already went down that path in that direction
    
    grid[row][col].directions_allowed |= 1 << dir;

    if(cur_char == '|'){
        if(dir == DOWN || dir == UP){ // keep going normally
            move_light(grid, dir == UP ? row-1 : row+1, col, rows, cols, dir);
        } else { // split
            move_light(grid, row-1, col, rows, cols, UP);
            move_light(grid, row+1, col, rows, cols, DOWN);
        }
    } else if(cur_char == '-'){ 
        if(dir == LEFT || dir == RIGHT){ // keep going normally
            move_light(grid, row, dir == LEFT ? col-1 : col+1, rows, cols, dir);
        } else { // split
            move_light(grid, row, col-1, rows, cols, LEFT);
            move_light(grid, row, col+1, rows, cols, RIGHT);
        }
    } else if(cur_char == '\\'){ // reflectors
        if(dir == UP) move_light(grid, row, col-1, rows, cols, LEFT);
        if(dir == DOWN) move_light(grid, row, col+1, rows, cols, RIGHT);    
        if(dir == LEFT) move_light(grid, row-1, col, rows, cols, UP);
        if(dir == RIGHT) move_light(grid, row+1, col, rows, cols, DOWN);
    } else if(cur_char == '/'){ // reflectors
        if(dir == UP) move_light(grid, row, col+1, rows, cols, RIGHT);
        if(dir == DOWN) move_light(grid, row, col-1, rows, cols, LEFT);
        if(dir == LEFT) move_light(grid, row+1, col, rows, cols, DOWN);
        if(dir == RIGHT) move_light(grid, row-1, col, rows, cols, UP);
    } else { // keep going normally
        int new_row = dir == UP ? row-1 : dir == DOWN ? row+1 : row;
        int new_col = dir == LEFT ? col-1 : dir == RIGHT ? col+1 : col;
        move_light(grid, new_row, new_col, rows, cols, dir);
    }

}

int get_end_grid_space_idx( int num_end_grid_spaces, int row, int col, int rows, int cols){
    if(row == 0) return col;
    else if(row == rows-1) return num_end_grid_spaces - cols + col;
    else if(col == 0) return rows + col - 1;
    else if(col == cols-1) return rows + cols - 2 + col - 1;
    else return -1;
}

int count_energized(grid_space **grid, int rows, int cols, end_grid_space **end_grid_spaces, int num_end_grid_spaces){
    int count = 0;
    for(int i=0; i<rows; ++i){ 
        for(int j=0; j<cols; ++j){
            if(grid[i][j].directions_allowed > 0) count++;
            if((end_grid_spaces != NULL) && (i == 0 || j == 0 || i == rows-1 || j == cols-1)){ // end grid space
                end_grid_space *egs; if((egs = malloc(sizeof(end_grid_space))) == NULL) unsafe_op("malloc");
                egs->row = i;
                egs->col = j;
                egs->dir = grid[i][j].directions_allowed;
                end_grid_spaces[get_end_grid_space_idx(num_end_grid_spaces, i, j, rows, cols)] = egs;
            }
        }
    }
    return count;
}

int init_and_count_grid(int rows, int cols, char **lines, int startRow, int startCol, char startDir, end_grid_space **end_grid_spaces, int num_end_grid_spaces){
    grid_space **grid; if((grid = malloc(rows * sizeof(grid_space*))) == NULL) unsafe_op("malloc");
    
    for(int i=0; i<rows; ++i){
        grid_space *row; if((row = malloc(cols * sizeof(grid_space))) == NULL) unsafe_op("malloc");
        for(int j=0; j<cols; ++j){
            grid_space gs = {lines[i][j], 0};
            row[j] = gs;
        }
        grid[i] = row;
    }

    move_light(grid, startRow, startCol, rows, cols, startDir);
    int partOneSum = count_energized(grid, rows, cols, end_grid_spaces, num_end_grid_spaces);
}

int main(int argc, char **argv){

    char *file_name = "../intest.txt";
    if(argc >= 2) file_name = argv[1];

    int buf_sz;
    int num_lines;

    char *buf = read_file(file_name, &buf_sz);
    char **lines = get_lines(buf, buf_sz, &num_lines);

    int rows = num_lines;
    int cols = strlen(lines[0]);
    int partOneSum = init_and_count_grid(rows, cols, lines, 0, 0, RIGHT, NULL, 0);

    int num_end_grid_spaces = 2 * rows + 2 * (cols - 2);
    end_grid_space **end_grid_spaces; if((end_grid_spaces = malloc(num_end_grid_spaces * sizeof(end_grid_space))) == NULL) unsafe_op("malloc");
    for(int i=0; i<num_end_grid_spaces; ++i) end_grid_spaces[i] = NULL;

    int partTwoSum = 0;
    // Top and bottom rows
    for(int i=0; i<cols; ++i){
        end_grid_space *egs = end_grid_spaces[get_end_grid_space_idx(num_end_grid_spaces, 0, i, rows, cols)];
        if(egs == NULL || ~(egs->dir >> UP & 1)){
            int num_energized = init_and_count_grid(rows, cols, lines, 0, i, DOWN, end_grid_spaces, num_end_grid_spaces);
            partTwoSum = partTwoSum < num_energized ? num_energized : partTwoSum;
        } 
        egs = end_grid_spaces[get_end_grid_space_idx(num_end_grid_spaces, rows-1, i, rows, cols)];
        if (egs == NULL  || ~(egs->dir >> DOWN & 1)) {
            int num_energized = init_and_count_grid(rows, cols, lines, rows-1, i, UP, end_grid_spaces, num_end_grid_spaces);
            partTwoSum = partTwoSum < num_energized ? num_energized : partTwoSum;
        }
    }
    // Left and right col
    for(int i=0; i<rows; ++i){
        end_grid_space *egs = end_grid_spaces[get_end_grid_space_idx(num_end_grid_spaces, i, 0, rows, cols)];
        if(egs == NULL  || ~(egs->dir >> LEFT & 1)){
            int num_energized = init_and_count_grid(rows, cols, lines, i, 0, RIGHT, end_grid_spaces, num_end_grid_spaces);
            partTwoSum = partTwoSum < num_energized ? num_energized : partTwoSum;
        } 
        egs = end_grid_spaces[get_end_grid_space_idx(num_end_grid_spaces, i, cols-1, rows, cols)];
        if(egs == NULL  || ~(egs->dir >> RIGHT & 1)) {
            int num_energized = init_and_count_grid(rows, cols, lines, i, cols-1, LEFT, end_grid_spaces, num_end_grid_spaces);
            partTwoSum = partTwoSum < num_energized ? num_energized : partTwoSum;
        }
    }


    printf("Part One: %d\n", partOneSum);
    printf("Part Two: %d\n", partTwoSum);

    return 0;
}