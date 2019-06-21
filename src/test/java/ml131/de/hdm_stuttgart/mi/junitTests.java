package ml131.de.hdm_stuttgart.mi;

import ml131.de.hdm_stuttgart.mi.JavaFxUI.UserInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import static junit.framework.TestCase.assertTrue;

public class junitTests {

    @Before
    public void setUp() throws IOException {

    }
    @Test
    public void amountOfCardsFiltered() throws IOException {
        UserInterface.fxWindow();

        Assert.assertEquals(searchEngine.cardcountFirstSearch,1904);
    }
}
