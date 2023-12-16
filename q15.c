#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdbool.h>

#define unsafe_op(e) {printf("Error with %s\n", e); exit(EXIT_FAILURE);}

#define SPLIT_BUFF 10
#define BUFFER 1024

#define HASH_INSERT_SUCCESS 0
#define HASH_INSERT_REPLACE 1 // For debugging
#define HASH_INSERT_FAILURE -1
#define HASH_REMOVE_SUCCESS 0
#define HASH_REMOVE_NOOP 1 // For debugging
#define HASH_REMOVE_FAILURE -1


#define NUM_BOXES 256

typedef struct lens {
    char *label;
    int focal_length;
    struct lens *prev; // Doubly linked, fuck you its easier for me
    struct lens *next;
} lens_t;

int make_lens(lens_t *lens, char *label, int focal_length, lens_t *prev, lens_t *next){
    lens->label = label;
    lens->focal_length = focal_length;
    lens->prev = prev;
    lens->next = next;
    return EXIT_SUCCESS;
}

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

int hash_str(char *str){
    int cur_val = 0;
    int l = strlen(str);

    for(int i=0; i<l; ++i){
        cur_val = ((cur_val + (int)str[i]) * 17) % 256;
    }

    return cur_val;
}

int insert_or_replace_hash(lens_t **hash_map, char *key, int val){
    int hash_idx = hash_str(key);
    lens_t *cur = hash_map[hash_idx];

    if(cur == NULL){ // Empty ll
        lens_t *lens;
        if((lens = malloc(sizeof(lens_t))) == NULL) unsafe_op("malloc");
        make_lens(lens, key, val, NULL, NULL);
        hash_map[hash_idx] = lens;
        return HASH_INSERT_SUCCESS;
    }

    lens_t *prev;
    // head is not null
    while(cur != NULL){
        if(strcmp(key, cur->label) == 0){ // replace
            cur->focal_length = val;
            return HASH_INSERT_REPLACE;
        }

        prev = cur;
        cur = cur->next;
    }

    lens_t *lens;
    if((lens = malloc(sizeof(lens_t))) == NULL) unsafe_op("malloc");
    make_lens(lens, key, val, prev, NULL);
    prev->next = lens;
    return HASH_INSERT_SUCCESS;
}

int remove_hash(lens_t **hash_map, char *key){
    int hash_idx = hash_str(key);
    lens_t *cur = hash_map[hash_idx];

    if(cur == NULL) return HASH_REMOVE_NOOP;

    if(strcmp(key, cur->label) == 0){ // remove head
        hash_map[hash_idx] = cur->next; 
        if(cur->next != NULL) cur->next->prev = NULL;
    }
    cur = cur->next;

    while(cur != NULL){
        if(strcmp(key, cur->label) == 0){ // remove
            if(cur->prev != NULL) cur->prev->next = cur->next;
            if(cur->next != NULL) cur->next->prev = cur->prev;
            free(cur);
            return HASH_INSERT_SUCCESS;
        }
        cur = cur->next;
    }

    return HASH_REMOVE_NOOP;
}

void print_hash(lens_t **hash_map, FILE *fp){
    fprintf(fp, "----------Hash Map----------\n");
    for(int i=0; i<NUM_BOXES; ++i){
        lens_t *cur = hash_map[i];
        int count = 1;
        
        while(cur != NULL){
            fprintf(fp, "Box: %d, Key: %s, Val: %d\n", i, cur->label, cur->focal_length);
            cur = cur->next;
        }
    }
    fprintf(fp, "----------------------------\n");
}


int main(int argc, char **argv){
    char *file_name = "../intest.txt";
    if(argc >= 2) file_name = argv[1];

    int buf_sz;
    int num_lines;

    char *buf = read_file(file_name, &buf_sz);
    char **lines = get_lines(buf, buf_sz, &num_lines);

    int partOneSum = 0;
    int partTwoSum = 0;

    lens_t **hash_map;
    if((hash_map = malloc(NUM_BOXES * sizeof(lens_t*))) == NULL) unsafe_op("malloc");
    for(int i=0; i<NUM_BOXES; ++i) hash_map[i] = NULL; // Set all heads to NULL

    for(int line_idx = 0; line_idx < num_lines; ++line_idx){ // Only one line for this one
        char *line = lines[line_idx];
        int num_splits;
        char **splits = split(line, ",", &num_splits);

        for(int i=0; i<num_splits; ++i){

            partOneSum += hash_str(splits[i]); // Part one

            // ------- Part Two -------
            int str_len = strlen(splits[i]);

            if (strchr(splits[i], '-') != NULL){ // remove operation
                char *sub_str;
                if((sub_str = malloc((str_len - 1) * sizeof(char))) == NULL) unsafe_op("malloc");
                strncpy(sub_str, splits[i], str_len-1);
                sub_str[str_len - 1] = '\0';

                int ret = remove_hash(hash_map, sub_str);
                if(ret == HASH_REMOVE_FAILURE) unsafe_op("remove_hash");
            } else { // insert operation
                int key_val_num;
                char **key_val = split(splits[i], "=", &key_val_num);
                if(key_val_num != 2) unsafe_op("split or some other thing higher up");

                int ret = insert_or_replace_hash(hash_map, key_val[0], atoi(key_val[1]));
                if(ret == HASH_INSERT_FAILURE) unsafe_op("insert_or_replace_hash");
            }
        }
    }
    
    // go through hash map
    for(int i=0; i<NUM_BOXES; ++i){
        lens_t *cur = hash_map[i];
        int count = 1;
        while(cur != NULL){
            partTwoSum += (i+1) * (count++) * cur->focal_length;
            cur = cur->next;
        }
    }

    printf("Part One: %d\n", partOneSum);
    printf("Part Two: %d\n", partTwoSum);

    return 0;
}