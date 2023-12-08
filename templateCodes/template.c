#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/stat.h>
#include <fcntl.h>

#define unsafe_op(e) {fprintf(stderr, "Error with %s\n", e); exit(EXIT_FAILURE);}

int main(void){

    char *file_name = "../in.txt";

    int fd;
    if((fd = open(file_name, O_RDONLY)) < 0) unsafe_op("open");

    struct stat sb;
    stat(file_name, &sb);
    intmax_t buf_sz = (intmax_t)sb.st_size;

    char *buf = malloc(buf_sz);
    read(fd, buf, buf_sz);

    for(int i=0; i<buf_sz; ++i) printf("%c", buf[i]);

    printf("\nbuf_sz: %d\n", buf_sz);

    return 0;
}