package cn.lsu.community.cache;

import cn.lsu.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {

    public static List<TagDTO> get(){
        List<TagDTO> list=new LinkedList<>();
        TagDTO program=new TagDTO();
        program.setId("program");
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("javascript","php","css","html","html5","java",
                "node.js","python","C","C++","golang","objective-c","typescript","shell",
                "swift","c#","sass","ruby","bash","less","asp.net","lua","scala","coffeescript","actionscriptrust","erlang","perl"));
        list.add(program);

        TagDTO framework=new TagDTO();
        framework.setId("framework");
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("laravel","spring","express","django","flask","ruby-on-rails","tornado","koa","strutslil"));
        list.add(framework);

        TagDTO sever=new TagDTO();
        sever.setId("sever");
        sever.setCategoryName("服务器");
        sever.setTags(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存","tomcat",
                "负载均衡", "unix" , "hadoop", "windows-server"));
        list.add(sever);

        TagDTO db=new TagDTO();
        db.setId("db");
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "nosgl memcached", "sglserver", "postgresgl", "sglite"
        ));

        list.add(db);

        TagDTO tool=new TagDTO();
        tool.setId("tool");
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git" , "github" ,"visual-studio-code" ,"vim" ,"sublime-text" ,"xcode intellij-idea" ,
                "eclipse" ,"maven" ,"ide" , "svn", "visual-studio" ,"atom emacs", "textmate" ,"hg"));
        list.add(tool);

        return list;
    }

    public static String filterInvalid(String tags){
        String[] split = StringUtils.split(tags,",");
        List<TagDTO> tagDTOS = TagCache.get();
        List<String> collects = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String invalid=Arrays.stream(split).filter(tag->!collects.contains(tag)).collect(Collectors.joining(","));
        return invalid;
    }
}
