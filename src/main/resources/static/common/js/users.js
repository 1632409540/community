/**
 * 添加用户
 */
function addUser() {
    var name = $('#name').val().trim();
    var password = $('#password').val().trim();
    if (name == ''){
        swal("警告","名称不能为空！", "warning");
        return;
    }
    if (password == ''){
        swal("警告","密码不能为空！", "warning");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/addUser",
        data: {name:name,password:password},
        dataType: "json",
        success: function(data){
            if (data['code']== 200){
                swal("添加成功", "你已经成功创建了用户"+name, "success");
                setInterval(reload, 2000);
            }else if (data['code']== 501){
                swal("添加失败", "用户名为"+name+"的用户已存在，请换一个新名称吧~", "error");
            }else{
                swal("出错啦", "服务器发生了一个错误", "error");
            }
        }
    });
}

/**
 * 刷新页面
 */
function reload() {
    window.location.reload();
}

/**
 * 上传用户头像
 */
function uploadUserImg() {
    var id = $('#id').val().trim();
    var formdata=new FormData();
    formdata.append("image",$("#img").get(0).files[0]);
    formdata.append("flag",2);
    formdata.append("id",id);
    $.ajax({
        async: false,
        type: "POST",
        url: "/image/upload",
        dataType: "json",
        data: formdata,
        contentType:false,//ajax上传图片需要添加
        processData:false,//ajax上传图片需要添加
        success: function (data) {
            if (data.code == 200){
                $('#img_head').attr("src",data.data);
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
 * 更新基本信息
 */
function updateUserBasic() {
    var id = $('#id').val().trim();
    var name = $('#name').val().trim();
    var password = $('#password').val().trim();
    var address = $('#address').val().trim();
    var bio = $('#bio').val().trim();
    var career = $('#career').val().trim();
    var birthdayStr = $('#birthdayStr').val().trim();
    var sex = $("input[name='sex']:checked").val();
    // 处理选填字段
    bio = bio == ''?' ':bio;
    address = address == ''?' ':address;
    career = career == ''?' ':career;

    if (name == ''){
        swal("请输入昵称", "昵称为必填项", "warning");
    }
    if(password == ''){
        swal("请输入密码", "密码为必填项", "warning");
    }
    if(name != ''&&password != ''){
        $.ajax({
            type: "POST",
            url: "/admin/user/update",
            data: {id:id,name:name,password:password,bio:bio,address:address,career:career,birthdayStr:birthdayStr,sex:sex,flag:'basic'},
            dataType: "json",
            success: function(data){
                if (data.code == 200){
                    swal("成功", "你已成功修改了基本信息", "success");
                }else if (data.code == 501){
                    swal("提示", "你没有修改任何信息", "info");
                }else if (data.code == 500){
                    swal("错误", "服务器发生了一个错误", "error");
                }
            }
        });
    }
}

/**
 * 更新联系方式
 */
function updateUserContact() {
    var id = $('#id').val().trim();
    var email = $('#email').val().trim();
    var phone = $('#phone').val().trim();
    var qq = $('#qq').val().trim();
    // 处理选填字段
    email = email == ''?' ':email;
    phone = phone == ''?' ':phone;
    qq = qq == ''?' ':qq;
    // 正则验证格式
    var emailReg = /^([\.a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
    var phoneReg = /^1[34578]\d{9}$/;
    var qqReg = /^[1-9][0-9]{4,9}$/;

    if(!emailReg.test(email)){
        swal("警告", "请输入正确的邮箱格式", "warning");
        return;
    }

    if (phone != ' '){
        if(!phoneReg.test(phone)){
            swal("警告", "请输入正确的手机号码", "warning");
            return;
        }
    }
    if (qq != " "){
        if(!qqReg.test(qq)){
            swal("警告", "请输入正确的QQ号码", "warning");
            return;
        }
    }

    $.ajax({
        type: "POST",
        url: "/admin/user/update",
        data: {id:id,email:email, phone:phone,qq:qq,flag:'contact'},
        dataType: "json",
        success: function(data){
            if (data.code== 200){
                swal("成功", "你已成功修改了联系方式", "success");
            }else if (data.code== 501){
                swal("提示", "你没有修改任何信息", "info");
            }else if (data.code== 500){
                swal("错误", "服务器发生了一个错误", "error");
            }
        }
    });
}
