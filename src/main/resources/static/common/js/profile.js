/**
 * 更新基本信息
 */
function updateBasic() {
    let name = $('#name').val().trim();
    let address = $('#address').val().trim();
    let bio = $('#bio').val().trim();
    let career = $('#career').val().trim();
    let birthdayStr = $('#birthdayStr').val().trim();
    let sex = $("input[name='sex']:checked").val();
    // 处理选填字段
    bio = bio == ''?' ':bio;
    address = address == ''?' ':address;
    career = career == ''?' ':career;

    if (name == ''){
        swal("请输入昵称", "昵称为必填项", "warning");
    }else {
        $.ajax({
            type: "POST",
            url: "/user/update",
            data: {name:name, bio:bio,address:address,career:career,birthdayStr:birthdayStr,sex:sex,flag:'basic'},
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
function updateContact() {
    let email = $('#email').val().trim();
    let phone = $('#phone').val().trim();
    let qq = $('#qq').val().trim();
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
        url: "/user/update",
        data: {email:email, phone:phone,qq:qq,flag:'contact'},
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

/**
 * 上传头像
 */
function uploadImg() {
    var formdata=new FormData();
    formdata.append("image",$("#img").get(0).files[0]);
    formdata.append("flag",1);
    $.ajax({
        async: false,
        type: "POST",
        url: "/image/upload",
        dataType: "json",
        data: formdata,
        contentType:false,//ajax上传图片需要添加
        processData:false,//ajax上传图片需要添加
        success: function (data) {
            console.log("data===>",data);
            if (data.code == 200){
                $('#img_head').attr("src",data.data);
                $('#img_head1').attr("src",data.data);
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
 * 通知设置
 */
function updateNotification() {
    let emailAnswer = $('#emailAnswer').val();
    let notifiAnswer = $('#notifiAnswer').val();
    let emailComment = $('#emailComment').val();
    let notifiComment = $('#notifiComment').val();
    let emailLike = $('#emailLike').val();
    let notifiLike = $('#notifiLike').val();
    let emailNewAnswer = $('#emailNewAnswer').val();
    let notifiNewAnswer = $('#notifiNewAnswer').val();
    let emailQuestion = $('#emailQuestion').val();
    let notifiQuestion = $('#notifiQuestion').val();

    $.ajax({
        type: "POST",
        url: "/user/update",
        data: {
            emailAnswer:emailAnswer,
            notifiAnswer:notifiAnswer,
            emailComment:emailComment,
            notifiComment:notifiComment,
            emailLike:emailLike,
            notifiLike:notifiLike,
            emailNewAnswer:emailNewAnswer,
            notifiNewAnswer:notifiNewAnswer,
            emailQuestion:emailQuestion,
            notifiQuestion:notifiQuestion,
            flag:'notification'
        },
        dataType: "json",
        success: function(data){
            if (data.code== 200){
                swal("成功", "保存通知设置成功！", "success");
            }else {
                swal("错误", "服务器发生了一个错误！", "error");
            }
        }
    });
}

/**
 * 修改密码
 */
function updatePassword() {
    let password = $('#password').val();
    let newPassword1 = $('#newPassword1').val();
    let newPassword2 = $('#newPassword2').val();

    if(password == ''||password.length < 0){
        swal("提示", "请输入您的当前密码！", "info");
    }
    if(newPassword1 == ''||newPassword1<0){
        swal("提示", "请输入您的新密码！", "info");
    }
    if(newPassword2 == ''||newPassword2<0){
        swal("提示", "请再次输入您的新密码！", "info");
    }
    if(password == newPassword1 ){
        swal("提示", "新密码与原密码相同，请重新输入！", "info");
    }
    if(newPassword1 != newPassword2 ){
        swal("提示", "两次输入的密码不一致，请重新输入！", "info");
    }
    $.ajax({
        type: "POST",
        url: "/user/update",
        data: {
            password:password,
            newPassword:newPassword1,
            flag:'password'
        },
        dataType: "json",
        success: function(data){
            if (data.code == 200){
                swal("成功", "密码修改成功", "success");
            }else if (data.code == 501){
                swal("错误", "你输入的当前密码不正确！", "error");
            }else {
                swal("错误", "服务器发生了一个错误！", "error");
            }
        }
    });
}