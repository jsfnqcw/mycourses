#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>

int lineCounter(char* fName){
  FILE* fp;
  int lCount=0;
  if((fp=fopen(fName,"r"))==NULL)
  {
      perror("CAN'T OPEN THE FILE");
      exit(-1);
  }
  while(!feof(fp)){
    if(fgetc(fp)=='\n')
      lCount++;
  }
  fclose(fp);
  return lCount;
}

int wordCounter(char* fName)
{
    FILE *fp;
    if((fp=fopen(fName,"r"))==NULL)
    {
        perror("CAN'T OPEN THE FILE");
        exit(-1);
    }
    char ch1,ch2=' ';
    int wCount=0;
    while(!feof(fp))
    {
        ch1=fgetc(fp);
        if(ch1!=' '&&ch1!='\t'&&ch1!='\v'&&ch1!='\r'&&ch1!='\n'&&ch1!='\f'&&ch1!=EOF&&
           (ch2==' '||ch2=='\t'||ch2=='\v'||ch2=='\r'||ch2=='\n'||ch2=='\f'))
        {
            wCount++;
        }
        ch2=ch1;
    }
    fclose(fp);
    return wCount;
}

int charCounter(char* fName)
{
    struct stat statbuf;
    lstat(fName,&statbuf);
    return statbuf.st_size;
}

int main(int argc,char *argv[]){
  if(argc==1){
    perror("MISSING FILENAME");
    return -1;
  }

  int fileNum=argc-1;
  char* files[fileNum];
  for(int i=0;i<fileNum;i++){
    files[i]=argv[i+1];
  }

  int total_line=0;
  int total_word=0;
  int total_char=0;

  for(int i=0;i<fileNum;i++){
    int lCount=lineCounter(files[i]);
    int wCount=wordCounter(files[i]);
    int cCount=charCounter(files[i]);
    printf("%d\t%d\t%d\t%s\n",lCount,wCount,cCount,files[i]);
    total_line+=lCount;
    total_word+=wCount;
    total_char+=cCount;
  }

  if(fileNum>1){
    printf("%d\t%d\t%d\ttotal\n",total_line,total_word,total_char);
  }

  return 0;
}
