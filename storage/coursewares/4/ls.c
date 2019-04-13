#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dirent.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <grp.h>
#include <pwd.h>
#include <time.h>
#include <stdlib.h>

#define MAXROWLEN  80

bool _l=false;
bool _d=false;
bool _R=false;
bool _a=false;
bool _i=false;
int g_leave_len = MAXROWLEN;    //一行剩余长度，用于输出对齐
int g_maxlen;   //存放某目录下最长的文件名的长度

void display_single(long inodeNum,char* name)   //打印文件
{

    if(_i){
      if(g_leave_len<g_maxlen+9+1)
      {
          printf("\n");
          g_leave_len=MAXROWLEN;
      }
    }
    else{
      if(g_leave_len<g_maxlen+1)
      {
          printf("\n");
          g_leave_len=MAXROWLEN;
      }
    }
    if(_i){
      printf("%*ld ",-8,inodeNum);
      g_leave_len-=9;
    }

    printf("%*s ",-g_maxlen,name);
    g_leave_len=g_leave_len-(g_maxlen+1);

}

//获取文件属性并打印
void display_attribute(struct stat buf,char* name)
{
    char buf_time[32];
    struct passwd *psd;     //从该结构体获取文件所有者的用户名
    struct group *grp;      //从该结构体获取文件所有者所属组的组名

    if(_i)
      printf("%*ld ",-8,buf.st_ino);

    //获取并打印文件类型
    if (S_ISLNK(buf.st_mode))
    {
        printf("l");
    }
    else if (S_ISREG(buf.st_mode))
    {
        printf("-");
    }
    else if (S_ISDIR(buf.st_mode))
    {
        printf("d");
    }
    else if(S_ISCHR(buf.st_mode))
    {
        printf("c");
    }
    else if (S_ISBLK(buf.st_mode))
    {
        printf("b");
    }
    else if (S_ISFIFO(buf.st_mode))
    {
        printf("f");
    }
    else if(S_ISSOCK(buf.st_mode))
    {
        printf("s");
    }

    //获取并打印文件所有者的权限
    if(buf.st_mode & S_IRUSR)
    {
        printf("r");
    }
    else
    {
        printf("-");
    }
    if(buf.st_mode & S_IWUSR)
    {
        printf("w");
    }
    else
    {
        printf("-");
    }
    if(buf.st_mode & S_IXUSR)
    {
        printf("x");
    }
    else
    {
        printf("-");
    }

    //获取并打印与文件所有者同组的用户对该文件的操作权限
    if(buf.st_mode & S_IRGRP)
    {
        printf("r");
    }
    else
    {
        printf("-");
    }
    if(buf.st_mode & S_IWGRP)
    {
        printf("w");
    }
    else
    {
        printf("-");
    }
    if(buf.st_mode & S_IXGRP)
    {
        printf("x");
    }
    else
    {
        printf("-");
    }

    //获取并打印其他用户对该文件的操作权限
    if (buf.st_mode & S_IROTH)
    {
        printf("r");
    }
    else
    {
        printf("-");
    }
    if (buf.st_mode & S_IWOTH)
    {
        printf("w");
    }
    else
    {
        printf("-");
    }
    if (buf.st_mode & S_IXOTH)
    {
        printf("x");
    }
    else
    {
        printf("-");
    }

    printf(" ");

    //根据uid与gid获取文件所有者的用户名与组名
    psd = getpwuid(buf.st_uid);
    grp = getgrgid(buf.st_gid);
    printf("%4d ", (int)buf.st_nlink);   //打印文件的链接数
    printf("%-8s", psd->pw_name);
    printf("%-8s", grp->gr_name);

    printf("%6d", buf.st_size);     //打印文件大小
    strcpy(buf_time, ctime(&buf.st_mtime));
    buf_time[strlen(buf_time) - 1] = '\0';  //取掉换行符
    printf("  %s", buf_time);   //打印文件的时间信息
    printf("  %s",name);
    printf("\n");
}

void normal_display(char* path){
  DIR *dir;
  struct dirent *ptr;
  struct stat statbuf;
  //获取该目录下文件总数和最长文件名
  dir = opendir(path);

  g_maxlen=0;
  while((ptr =readdir(dir)) != NULL)
  {
    if(g_maxlen < strlen(ptr->d_name))
    {
        g_maxlen = strlen(ptr->d_name);
    }
  }

  rewinddir(dir);

  char* fileNames[80];
  int count=0;
  while((ptr =readdir(dir)) != NULL)
  {
    if(!_a&&ptr->d_name[0]=='.'){
      continue;
    }
    fileNames[count]=ptr->d_name;
    count++;
  }

  char* temp;
  for(int i=0;i<count-1;i++)
  {
      for(int j=0;j<count-1-i;j++)
      {
        if(strcmp(fileNames[j],fileNames[j+1])>0)
        {
          temp=fileNames[j];
          fileNames[j]=fileNames[j+1];
          fileNames[j+1]=temp;
        }
      }
  }
  char* Path = (char *) malloc(strlen(path) + g_maxlen + 2);
  for(int i=0;i<count;i++){
    sprintf(Path, "%s/%s", path, fileNames[i]);
    lstat(Path,&statbuf);
    display_single(statbuf.st_ino,fileNames[i]);
  }
  closedir(dir);
  if(count!=0)
    printf("\n");
}

void longtext_display(char* path){
  DIR *dp;
  struct dirent *entry;
  struct stat statbuf;
  long total = 0;
  if ((dp = opendir(path)) == NULL) {
    perror("opendir fail!");
  }

  g_maxlen=0;
  while ((entry = readdir(dp)) != NULL) {
    if(!_a&&entry->d_name[0]=='.'){
      continue;
    }
    if(g_maxlen < strlen(entry->d_name))
    {
        g_maxlen = strlen(entry->d_name);
    }
    char* Path = (char *) malloc(strlen(path) + g_maxlen + 2);
    sprintf(Path, "%s/%s", path, entry->d_name);
    lstat(Path, &statbuf);
    long nBlocks = statbuf.st_size / 4096;
    if (statbuf.st_size % 4096 > 0)
      nBlocks++;
    if (S_ISLNK(statbuf.st_mode))
      nBlocks = 0;
    total += nBlocks*(statbuf.st_blksize/1024);
  }
  rewinddir(dp);
  printf("total %ld\n",total);


  char* fileNames[80];
  int count=0;
  while((entry =readdir(dp)) != NULL)
  {
    if(!_a&&entry->d_name[0]=='.'){
      continue;
    }
    fileNames[count]=entry->d_name;
    count++;
  }

  char* temp;

  for(int i=0;i<count-1;i++)
  {
      for(int j=0;j<count-1-i;j++)
      {
        if(strcmp(fileNames[j],fileNames[j+1])>0)
        {
          temp=fileNames[j];
          fileNames[j]=fileNames[j+1];
          fileNames[j+1]=temp;
        }
      }
  }
  char* Path = (char *) malloc(strlen(path) + g_maxlen + 2);
  for(int i=0;i<count;i++){
    sprintf(Path, "%s/%s", path, fileNames[i]);
    lstat(Path, &statbuf);
    display_attribute(statbuf,fileNames[i]);
  }

  closedir(dp);
}

void recursive(char* path){
  printf("%s:\n",path);
  if(_l){
    longtext_display(path);
  }else{
    normal_display(path);
  }
  printf("\n");

  DIR *dp;
  struct dirent *entry;
  struct stat statbuf;
  long total = 0;
  if ((dp = opendir(path)) == NULL) {
    perror("opendir fail!");
  }

  while ((entry = readdir(dp)) != NULL) {
    if(!_a&&entry->d_name[0]=='.'){
      continue;
    }
    char* Path = (char *) malloc(strlen(path) + g_maxlen + 2);
    sprintf(Path, "%s/%s", path, entry->d_name);
    lstat(Path, &statbuf);
    if(S_ISDIR(statbuf.st_mode)&&(strcmp(entry->d_name,".")!=0)&&(strcmp(entry->d_name,"..")!=0)){
      recursive(Path);
    }
  }
  closedir(dp);
}

int main(int argc,char *argv[]){

  int index;
	for(index=1;index<argc;index++){
		if(argv[index][0]=='-'){
			if(strlen(argv[index])!=2){
				printf("No support for linked argument:%s\n",argv[index]);
				return -1;
			}else{
				switch (argv[index][1]) {
					case 'l':
						_l=true;
						break;
					case 'd':
						_d=true;
						break;
					case 'R':
						_R=true;
						break;
					case 'a':
						_a=true;
						break;
					case 'i':
						_i=true;
						break;
					default:
						printf("Unknown argument type:%s\n",argv[index]);
						return -1;
				}
			}
		}else{
			break;
		}
	}

  int pathNum=argc-index;
  char* paths[pathNum];
  for(int i=0;index<argc;index++,i++){
    paths[i]=argv[index];
  }
  if(_d){
    struct stat statbuf;
    if(pathNum==0){
      if(_l){
        lstat(".",&statbuf);
        display_attribute(statbuf,".");
      }else{
        lstat(".",&statbuf);
        display_single(statbuf.st_ino,".");
        printf("\n");
      }
    }else{
      for(int i=0;i<pathNum;i++){
        if(g_maxlen < strlen(paths[i])){
            g_maxlen = strlen(paths[i]);
        }
      }
      for(int i=0;i<pathNum;i++){
        if(_l){
          lstat(paths[i],&statbuf);
          display_attribute(statbuf,paths[i]);
        }else{
          lstat(paths[i],&statbuf);
          display_single(statbuf.st_ino,paths[i]);
        }
      }
      if(!_l)
        printf("\n");
    }
  }else{
    if(_R){
      if(pathNum==0){
        recursive(".");
      }
      else{
        for(int i=0;i<pathNum;i++){
          recursive(paths[i]);
        }
      }
    }else{
      if(pathNum==0){
        if(_l){
          longtext_display(".");
        }else{
          normal_display(".");
        }
      }else{
        for(int i=0;i<pathNum;i++){
          if(pathNum!=1)
            printf("%s:\n",paths[i]);
          if(_l){
            longtext_display(paths[i]);
          }else{
            normal_display(paths[i]);
          }
          if(i!=pathNum-1)
            printf("\n");
        }
      }
    }
  }

	return 0;
}
