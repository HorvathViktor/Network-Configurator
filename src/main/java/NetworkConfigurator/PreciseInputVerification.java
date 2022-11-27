package main.java.NetworkConfigurator;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.java.BasicNetworking.IPAddressValidator;
import main.java.BasicNetworking.InvalidIPException;
import main.java.GUI.Input.EmptyOrNullInputException;
import org.jetbrains.annotations.NotNull;

public class PreciseInputVerification
{
    private final ListView<String> list;
    public boolean isFlawless = true;

    public PreciseInputVerification(ListView<String> list)
    {
        this.list = list;
    }

    public void verifyObligatoryStringInput(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            writeToList(location + textField.getId() + " was empty");
            makeTextFieldRed(textField);
        }
        else
        {
            makeTextFieldNormal(textField);
        }
    }

    public void verifyObligatoryPositiveOrZeroIntegerInput(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            writeToList(location + textField.getId() + " was empty");
            makeTextFieldRed(textField);
            return;
        }
        try
        {
            int parsed = Integer.parseInt(textField.getText());
            if (parsed < 0)
            {
                writeToList(location + textField.getId() + " was negative");
                makeTextFieldRed(textField);
            }
            else
            {
                makeTextFieldNormal(textField);
            }
        }
        catch (NumberFormatException e)
        {
            writeToList(location + textField.getId() + " was an invalid number");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryPositiveOrZeroIntegerInput(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            makeTextFieldNormal(textField);
            return;
        }
        try
        {
            int parsed = Integer.parseInt(textField.getText());
            if (parsed < 0)
            {
                writeToList(location + textField.getId() + " was negative");
                makeTextFieldRed(textField);
            }
            else
            {
                makeTextFieldNormal(textField);
            }
        }
        catch (NumberFormatException e)
        {
            writeToList(location + textField.getId() + " was an invalid number");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryPositiveIntegerInput(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            makeTextFieldNormal(textField);
            return;
        }
        try
        {
            int parsed = Integer.parseInt(textField.getText());
            if (parsed <= 0)
            {
                writeToList(location + textField.getId() + " was negative");
                makeTextFieldRed(textField);
            }
            else
            {
                makeTextFieldNormal(textField);
            }
        }
        catch (NumberFormatException e)
        {
            writeToList(location + textField.getId() + " was an invalid number");
            makeTextFieldRed(textField);
        }
    }

    public void verifyObligatoryCustomRangeIntegerInput(String location, TextField textField, int lowerRange, int upperRange)
    {
        if (isEmptyOrNullInput(textField))
        {
            writeToList(location + textField.getId() + " was empty");
            makeTextFieldRed(textField);
            return;
        }
        try
        {
            int parsed = Integer.parseInt(textField.getText());
            if (parsed < lowerRange || parsed > upperRange)
            {
                writeToList(location + textField.getId() + " was not between " + lowerRange + "-" + upperRange);
                makeTextFieldRed(textField);
            }
            else
            {
                makeTextFieldNormal(textField);
            }
        }
        catch (NumberFormatException e)
        {
            writeToList(location + textField.getId() + " was an invalid number");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryCustomRangeIntegerInput(String location, TextField textField, int lowerRange, int upperRange)
    {
        if (isEmptyOrNullInput(textField))
        {
            makeTextFieldNormal(textField);
            return;
        }
        try
        {
            int parsed = Integer.parseInt(textField.getText());
            if (parsed < lowerRange || parsed > upperRange)
            {
                writeToList(location + textField.getId() + " was not between " + lowerRange + "-" + upperRange);
                makeTextFieldRed(textField);
            }
            else
            {
                makeTextFieldNormal(textField);
            }
        }
        catch (NumberFormatException e)
        {
            writeToList(location + textField.getId() + " was an invalid number");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryCoDependentPositiveOrZeroIntegerInput(String location, TextField textField1, TextField textField2)
    {
        boolean isFirstEmpty = false;
        boolean isSecondEmpty = false;
        if (isEmptyOrNullInput(textField1))
        {
            isFirstEmpty = true;
        }
        if (isEmptyOrNullInput(textField2))
        {
            isSecondEmpty = true;
        }
        if (isFirstEmpty && isSecondEmpty)
        {
            makeTextFieldNormal(textField1);
            makeTextFieldNormal(textField2);
            return;
        }
        if (!isFirstEmpty && isSecondEmpty)
        {
            makeTextFieldRed(textField1);
            makeTextFieldRed(textField2);
            writeToList(location + textField1.getId() + " was not empty but it's continuation " + textField2.getId() + " was");
            return;
        }
        if (isFirstEmpty)
        {
            makeTextFieldRed(textField1);
            makeTextFieldRed(textField2);
            writeToList(location + textField1.getId() + " was empty but it's continuation " + textField2.getId() + " was not");
            return;
        }
        try
        {
            int parsed = Integer.parseInt(textField1.getText());
            if (parsed < 0)
            {
                writeToList(location + textField1.getId() + " was negative");
                makeTextFieldRed(textField1);
            }
            else
            {
                makeTextFieldNormal(textField1);
            }
        }
        catch (NumberFormatException e)
        {
            writeToList(location + textField1.getId() + " was an invalid number");
            makeTextFieldRed(textField1);
        }
        try
        {
            int parsed = Integer.parseInt(textField2.getText());
            if (parsed < 0)
            {
                writeToList(location + textField2.getId() + " was negative");
                makeTextFieldRed(textField2);
            }
            else
            {
                makeTextFieldNormal(textField2);
            }
        }
        catch (NumberFormatException e)
        {
            writeToList(location + textField2.getId() + " was an invalid number");
            makeTextFieldRed(textField2);
        }
    }

    public void verifyObligatoryIPv4Address(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            writeToList(location + textField.getId() + " was empty");
            makeTextFieldRed(textField);
            return;
        }
        try
        {
            IPAddressValidator.isValidIPv4(textField.getText());
            makeTextFieldNormal(textField);
        }
        catch (InvalidIPException e)
        {
            writeToList(location + textField.getId() + " had an invalid IPv4 address");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryIPv4Address(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            makeTextFieldNormal(textField);
            return;
        }
        try
        {
            IPAddressValidator.isValidIPv4(textField.getText());
            makeTextFieldNormal(textField);
        }
        catch (InvalidIPException e)
        {
            writeToList(location + textField.getId() + " had an invalid IPv4 address");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryIPv4Network(String location, TextField ip, TextField prefix)
    {
        if (isEmptyOrNullInput(ip) && isEmptyOrNullInput(prefix))
        {
            makeTextFieldNormal(ip);
            makeTextFieldNormal(prefix);
            return;
        }
        try
        {

            if (prefix.getText().split("\\.").length == 4)
            {
                IPAddressValidator.isValidIpv4Network(ip.getText(), prefix.getText());
            }
            else
            {
                String mask = prefix.getText();
                if (mask.contains("/"))
                {
                    mask = mask.substring(1);
                }
                mask = IPAddressValidator.convertIpv4PrefixToMask(mask);
                IPAddressValidator.isValidIpv4Network(ip.getText(), mask);
            }

            makeTextFieldNormal(ip);
            makeTextFieldNormal(prefix);
        }
        catch (EmptyOrNullInputException | IllegalArgumentException e)
        {
            writeToList(location + ip.getId() + " had an invalid IPv4 address or mask");
            makeTextFieldRed(ip);
            makeTextFieldRed(prefix);
        }
    }

    public void verifyObligatoryIPv6Address(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            writeToList(location + textField.getId() + " was empty");
            makeTextFieldRed(textField);
            return;
        }
        try
        {
            IPAddressValidator.isValidIPv6(textField.getText());
            makeTextFieldNormal(textField);
        }
        catch (InvalidIPException e)
        {
            writeToList(location + textField.getId() + " had an invalid IPv6 address");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryIPv6Address(String location, TextField textField)
    {
        if (isEmptyOrNullInput(textField))
        {
            makeTextFieldNormal(textField);
            return;
        }
        try
        {
            IPAddressValidator.isValidIPv6(textField.getText());
            makeTextFieldNormal(textField);
        }
        catch (InvalidIPException e)
        {
            writeToList(location + textField.getId() + " had an invalid IPv6 address");
            makeTextFieldRed(textField);
        }
    }

    public void verifyNonObligatoryIPv6Network(String location, TextField ip, TextField prefix)
    {
        if (isEmptyOrNullInput(ip) && isEmptyOrNullInput(prefix))
        {
            makeTextFieldNormal(ip);
            makeTextFieldNormal(prefix);
            return;
        }
        try
        {
            String mask = prefix.getText();
            if (mask.contains("/"))
            {
                mask = mask.substring(1);
            }
            IPAddressValidator.isValidIpv6Network(ip.getText(), Integer.parseInt(mask));
            makeTextFieldNormal(ip);
            makeTextFieldNormal(prefix);
        }
        catch (EmptyOrNullInputException | IllegalArgumentException | InvalidIPException e)
        {
            writeToList(location + ip.getId() + " had an invalid IPv6 address or prefix");
            makeTextFieldRed(ip);
            makeTextFieldRed(prefix);
        }
    }

    private void writeToList(String string)
    {
        list.getItems().add(string);
    }

    private boolean isEmptyOrNullInput(@NotNull TextField textField)
    {
        return textField.getText() == null || textField.getText().isEmpty();
    }

    private void makeTextFieldRed(@NotNull TextField textField)
    {
        Border redBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1)));
        textField.setBorder(redBorder);
        isFlawless = false;
    }

    private void makeTextFieldNormal(@NotNull TextField textField)
    {
        TextField textField1 = new TextField();
        textField.setBorder(textField1.getBorder());
    }
}
