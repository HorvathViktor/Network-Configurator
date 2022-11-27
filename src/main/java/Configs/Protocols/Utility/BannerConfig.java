package main.java.Configs.Protocols.Utility;

import main.java.Configs.Config;

public class BannerConfig extends Config
{

    private String motd;
    private String exec;
    private String login;

    public BannerConfig withMOTDBanner(String motd)
    {
        this.motd = motd;
        return this;
    }

    public BannerConfig withExecBanner(String exec)
    {
        this.exec = exec;
        return this;
    }

    public BannerConfig withLoginBanner(String login)
    {
        this.login = login;
        return this;
    }

    private void setMOTDBanner()
    {
        send("banner motd #"+motd+"#","config");
    }

    private void setExecBanner()
    {
        send("banner exec #"+exec+"#", "config");
    }

    private void setLoginBanner()
    {
        send("banner #"+login+"#", "config");
    }

    @Override
    public void work()
    {
        if(isNotEmptyOrNull(motd) || isNotEmptyOrNull(exec) || isNotEmptyOrNull(login))
        {
            logger.write("-- BEGIN BANNER CONFIG --");
            configure();
            if (isNotEmptyOrNull(motd))
            {
                setMOTDBanner();
            }
            if (isNotEmptyOrNull(exec))
            {
                setExecBanner();
            }
            if (isNotEmptyOrNull(login))
            {
                setLoginBanner();
            }
            end();
        }
    }
}
