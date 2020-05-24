/**
 * 提交回复
 */
function send() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    doComment(questionId,1,content);
}

/**
 * 执行回复具体功能
 * @param targetId
 * @param type
 * @param content
 */
function doComment(targetId,type,content) {
    if(!content){
        alert("回复内容不能为空！！！");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                $("#comment_section").hide();
                window.location.reload();
            } else {
                if (response.code == 2004) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=b3f5fa1c25e7ecd68dd0&\n" +
                            "redirect_uri=http://localhost:8090/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
            console.log(response);
        },
        dataType: "json"
    });
}

/**
 * 实现二级回复
 */
function secondComment(e) {
    var id=e.getAttribute("data-id");
    var content=$("#input-"+id).val();
    doComment(id,2,content);
}
/**
 * 展开二级回复
 */
function collapseComment(e) {
    var id=e.getAttribute("data-id");
    var comments=$("#comment-"+id);
    console.log(id);
    // 获取二级评论状态
    var collapse=e.getAttribute("in");
    if(collapse){
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("in");
        comments.addClass("active");
    }else {
        //展开二级评论
        //获取二级评论数据
        $.getJSON("/comment/"+id,function (data) {
            //向html中展示数据
            viewSecondComment(id,data);
            comments.addClass("in");
            e.setAttribute("in","true");
            comments.removeClass("active");
        });
    }
}

function viewSecondComment(id,data) {
    var text='';
    var secondComments=data.data;
    var thisComment=$("#cansel-"+id);
    for(var index=0;index<secondComments.length;index++){
        var unixtimestamp = new Date(secondComments[index].gmtCreate);
        console.log(unixtimestamp);
        var year = unixtimestamp.getFullYear();
        var month = "0" + (unixtimestamp.getMonth() + 1);
        var day = "0" + unixtimestamp.getDate();
        var date=year + "-" + month.substring(month.length-2, month.length)  + "-" + day.substring(day.length-2, day.length);
        text+='<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 comments">\n' +
            '                                <div class="media-left">\n' +
            '                                    <a href="#">\n' +
            '                                        <img class="media-object img-rounded" src="'+secondComments[index].user.avatarUrl+'">\n' +
            '                                    </a>\n' +
            '                                </div>\n' +
            '                                <div class="media-body">\n' +
            '                                    <span class="text-desc">\n' +
            '                                        <span>'+secondComments[index].user.name+'</span>\n' +
            '                                    </span>\n' +
            '                                    <div>'+secondComments[index].content+'</div>\n' +
            '                                    <div class="menu">\n' +
            '                                        <span class="pull-right">'+date+'</span>\n' +
            '                                    </div>\n' +
            '                                </div>\n' +
            '                            </div>';
    }
    text+='<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">\n' +
        '                                <input type="text" class="form-control" placeholder="评论一下......" id="input-'+id+'"/>\n' +
        '                                <div class="btn-comment">\n' +
        '                                    <button type="button" class="btn btn-success btn-sm" onclick="secondComment(this)" data-id="'+id+'">回复</button>\n' +
        '                                    <button type="button" class="btn btn-default btn-sm" onclick="collapseComment('+thisComment+')" data-id="'+id+'">取消</button>\n' +
        '                                </div>\n' +
        '                            </div>';
    $("#comment-"+id).html(text);
}