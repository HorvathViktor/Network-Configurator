package main.java.Configs.Protocols.Utility;

import main.java.Configs.Config;

public class UserConfig extends Config
{
    private final String username;
    private String password;
    private boolean secret;

    public UserConfig(String username, String password, boolean secret)
    {
        this.username = username;
        this.password = password;
        this.secret = secret;
    }

    private void addUserWithSecretPassword()
    {
        send("username " + username + " secret " + password, "config");
    }

    private void addUserWithPassword()
    {
        send("username " + username + " password " + password, "config");
    }

    private void addUser()
    {
        send("username " + username, "config");
    }



    @Override
    public void work()
    {
        logger.write("-- BEGIN USER CONFIG --");
        configure();
        if (isNotEmptyOrNull(password))
        {
            if (secret)
            {
                addUserWithSecretPassword();
            }
            else
            {
                addUserWithPassword();
            }
        }
        else
        {
            addUser();
        }
        end();
    }
}
