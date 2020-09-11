package main.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "mail.credentials")
@ConstructorBinding
public class EmailCfg {
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public EmailCfg(String host, int port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    public String getHost(){
        return host;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }public int getPort(){
        return port;
    }




}