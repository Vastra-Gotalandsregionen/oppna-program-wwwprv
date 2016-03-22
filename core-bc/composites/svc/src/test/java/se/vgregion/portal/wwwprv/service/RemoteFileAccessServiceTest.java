package se.vgregion.portal.wwwprv.service;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Patrik Björk
 */
public class RemoteFileAccessServiceTest {

    @Test
    public void complementFileNameWithNamndfordelningPart() throws Exception {

        // Happy days
        String fileName = "UNILABS_LAB.INPUT_20160322_1319.IN";
        String newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
        assertEquals("UNILABS_LAB.INPUT_20160322_1319.IN_Nämndfördelad.csv", newName);

        // No dot - add last.
        fileName = "UNILABS_LABINPUT_20160322_1319IN";
        newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
        assertEquals("UNILABS_LABINPUT_20160322_1319IN_Nämndfördelad.csv", newName);

        // Empty - add last.
        fileName = "";
        newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
        assertEquals("_Nämndfördelad.csv", newName);

        // Null - no change
        fileName = null;
        try {
            newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
            fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }
}