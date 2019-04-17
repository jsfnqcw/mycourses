function loadCourseImg(){
    var url=window.location.href;
    $.get('/getCourseImg?curriculumId='+url.substr(url.lastIndexOf('/')+1), function(res){
        $('.imgWrap img').attr('src',res.msg);
    });
};