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

var myMap=new Map();
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
        myMap.remove(id);
    }else {
        //展开二级评论
        //获取二级评论数据
        $.getJSON("/comment/"+id,function (data) {
            //向html中展示数据
            viewSecondComment(id,data);
            comments.addClass("in");
            e.setAttribute("in","true");
            comments.removeClass("active");
            myMap.put(id,e);
        });
    }
}
function closeComment(id) {
    var e=myMap.get(id);
    var comments=$("#comment-"+id);
    comments.removeClass("in");
    e.removeAttribute("in");
    comments.addClass("active");
    myMap.remove(id);
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
        '                                    <button type="button" class="btn btn-default btn-sm" onclick="closeComment('+id+')">取消</button>\n' +
        '                                </div>\n' +
        '                            </div>';
    $("#comment-"+id).html(text);
}

/**
 * 回复点赞
 */
function commentThumbsUp(e) {
    var id=e.getAttribute("data-id");
    $.getJSON("/commentThumbsUp/"+id,function (data) {
        //向html中展示数据
        // console.log(data.data);
        var likeCount=data.data+"";
        $("#like-"+id).html(likeCount);
    });
}
/**
 * 添加标签
 */
function selectTag(e) {
    var tags=$("#tag").val();
    var tag=e.getAttribute("data-tag");
    if(tags.indexOf(tag)==-1){
        if(tags){
            $("#tag").val(tags+","+tag);
        }else {
            $("#tag").val(tag);
        }
    }
}

function showSelectTag() {
    $("#select-tag").show();
}

function Map() {
    /** 存放键的数组(遍历用到) */
    this.keys = new Array();
    /** 存放数据 */
    this.data = new Object();

    /**
     * 放入一个键值对
     * @param {String} key
     * @param {Object} value
     */
    this.put = function(key, value) {
        if(this.data[key] == null){
            this.keys.push(key);
        }
        this.data[key] = value;
    };

    /**
     * 获取某键对应的值
     * @param {String} key
     * @return {Object} value
     */
    this.get = function(key) {
        return this.data[key];
    };

    /**
     * 删除一个键值对
     * @param {String} key
     */
    this.remove = function(key) {
       // this.keys.remove(key);
        this.data[key] = null;
    };

    /**
     * 遍历Map,执行处理函数
     *
     * @param {Function} 回调函数 function(key,value,index){..}
     */
    this.each = function(fn){
        if(typeof fn != 'function'){
            return;
        }
        var len = this.keys.length;
        for(var i=0;i<len;i++){
            var k = this.keys[i];
            fn(k,this.data[k],i);
        }
    };

    /**
     * 获取键值数组(类似<a href="http://lib.csdn.net/base/java" class='replace_word' title="Java 知识库" target='_blank' style='color:#df3434; font-weight:bold;'>Java</a>的entrySet())
     * @return 键值对象{key,value}的数组
     */
    this.entrys = function() {
        var len = this.keys.length;
        var entrys = new Array(len);
        for (var i = 0; i < len; i++) {
            entrys[i] = {
                key : this.keys[i],
                value : this.data[i]
            };
        }
        return entrys;
    };

    /**
     * 判断Map是否为空
     */
    this.isEmpty = function() {
        return this.keys.length == 0;
    };

    /**
     * 获取键值对数量
     */
    this.size = function(){
        return this.keys.length;
    };

    /**
     * 重写toString
     */
    this.toString = function(){
        var s = "{";
        for(var i=0;i<this.keys.length;i++,s+=','){
            var k = this.keys[i];
            s += k+"="+this.data[k];
        }
        s+="}";
        return s;
    };
}