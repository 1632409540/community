/**
 * 上传系统logo图片
 */
function uploadSystemImg(flag) {
    var formdata=new FormData();
    if(flag ==3){
        formdata.append("image",$("#systemLogo").get(0).files[0]);
    }else if (flag == 4) {
        formdata.append("image",$("#background").get(0).files[0]);
    }else if (flag == 5) {
        formdata.append("image",$("#public_wechat").get(0).files[0]);
    }else if (flag == 6) {
        formdata.append("image",$("#publicMicroblog").get(0).files[0]);
    }else if (flag == 7) {
        formdata.append("image",$("#publicQq").get(0).files[0]);
    }

    formdata.append("flag",flag);
    $.ajax({
        async: false,
        type: "POST",
        url: "/admin/image/upload",
        dataType: "json",
        data: formdata,
        contentType:false,//ajax上传图片需要添加
        processData:false,//ajax上传图片需要添加
        success: function (data) {
            if (data.code == 200){
                if(flag ==3){
                    $('#systemLogoImg').attr("src",data.data);
                }else if (flag == 4) {
                    $('#img_background').attr("src",data.data);
                }else if (flag == 5) {
                    $('#img_public_wechat').attr("src",data.data);
                }else if (flag == 6) {
                    $('#publicMicroblogImg').attr("src",data.data);
                }else if (flag == 7) {
                    $('#publicQqImg').attr("src",data.data);
                }
                swal("成功", "上传成功", "success");
            }else if (data.code == 501){
                swal("错误", data.message, "error");
            }else {
                swal("错误", "文件过大，请上传小于1M的图片", "error");
            }
        },
        error: function (e) {
        }
    });
}


/**
 * 更新前台信息
 */
function updateSystemTitle() {
    var title = $('#title').val().trim();
    if (title == ''){
        swal("警告", "标题不能为空", "warning");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/admin/updateSystemTitle",
        dataType: "json",
        data: {title:title},
        success: function (data) {
            if (data['code']== 200){
                swal("更新成功", "", "success");

            }else {
                swal("错误", "服务器发生了一个错误", "error");
            }
        },
        error: function (e) {
        }
    });
}
/**
 * 更新前台信息
 */
function updateSystemPublicTitle() {
    var title = $('#publicTitle').val().trim();
    if (title == ''){
        swal("警告", "标题不能为空", "warning");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/admin/updateSystemPublicTitle",
        dataType: "json",
        data: {title:title},
        success: function (data) {
            if (data['code']== 200){
                swal("更新成功", "", "success");
            }else {
                swal("错误", "服务器发生了一个错误", "error");
            }
        },
        error: function (e) {
        }
    });
}

/**
 * 添加友情链接
 */
function addLink() {

    var name = $('#name').val().trim();
    var target = $('#target').val().trim();

    if (name == ''){
        swal("警告", "标题不能为空", "warning");
        return;
    }
    if (target == ''){
        swal("警告", "链接地址不能为空", "warning");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/admin/addLink",
        dataType: "json",
        data: {name:name,url:target},
        success: function (data) {
            if (data['code']== 200){
                swal("添加成功", "", "success");
                setInterval(reload, 2000);
            }else {
                swal("错误", "服务器发生了一个错误", "error");
            }
        },
        error: function (e) {
        }
    });
}

/**
 * 删除友情链接
 * @param id
 */
function deleteLink(id) {
    swal({
        title: "确定删除此链接?",
        text: "此操作不可恢复！",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    })
        .then((willDelete) => {
            if (willDelete) {
                $.ajax({
                    type: "GET",
                    url: "/admin/deleteLink",
                    dataType: "json",
                    data: {id:id},
                    success: function (data) {
                        if (data['code']== 200){
                            swal("删除成功", "", "success");
                            setInterval(reload, 2000);
                        }else {
                            swal("错误", "服务器发生了一个错误", "error");
                        }
                    },
                    error: function (e) {
                    }
                });
            }
        });
}

/**
 * 刷新页面
 */
function reload() {
    window.location.reload();
}