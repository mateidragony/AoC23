#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/stat.h>
#include <fcntl.h>

#define unsafe_op(e) {fprintf(stderr, "Error with %s\n", e); exit(EXIT_FAILURE);}

#define SPLIT_BUFF 10
#define BUFFER 1024

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
                if((cur_wrd = realloc(cur_wrd, cur_wrd_buf_sz)) == NULL) unsafe_op("realloc");
            }
        } else { // new word
            cur_wrd[cur_wrd_idx] = '\0';
            lines[(*num_lines)++] = cur_wrd;

            if(num_lines >= line_buf_sz){
                line_buf_sz += BUFFER;
                if((cur_wrd = realloc(lines, line_buf_sz)) == NULL) unsafe_op("realloc");
            }

            cur_wrd_idx = 0;
            if((cur_wrd = malloc(BUFFER * sizeof(char))) == NULL) unsafe_op("malloc");
        }
    }

    cur_wrd[cur_wrd_idx] = '\0';
    lines[(*num_lines)++] = cur_wrd;
    cur_wrd_idx = 0;

    free(buf); 
    buf = NULL;

    return lines;
}


char **split(char *str, const char *split_str){
    char **splits; if((splits = malloc(SPLIT_BUFF * sizeof(char*))) == NULL) unsafe_op("malloc");
    const int len = strlen(str);

    int cur_wrd_idx = 0;
    int cur_wrd_buf_sz = BUFFER;
    char *cur_wrd; if((cur_wrd = malloc(BUFFER * sizeof(char))) == NULL) unsafe_op("malloc");

    for(int i=0; i<)    
}


int main(int argc, char **argv){

    char *file_name = "../intest.txt";
    if(argc >= 2) file_name = argv[1];

    int buf_sz;
    int num_lines;

    char *buf = read_file(file_name, &buf_sz);
    char **lines = get_lines(buf, buf_sz, &num_lines);
    

    for(int i=0; i<num_lines; ++i) printf("%s\n", lines[i]);

    return 0;
}