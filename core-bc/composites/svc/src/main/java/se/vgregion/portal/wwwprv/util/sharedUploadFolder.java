package se.vgregion.portal.wwwprv.util;

/**
 * @author Patrik Bergström
 */
public enum SharedUploadFolder {

    MARS_SHARED_FOLDER((short) 0, "Mars"), AVESINA_SHARED_FOLDER((short) 1, "Avesina");

    private final short index;
    private final String label;

    SharedUploadFolder(short index, String label) {
        this.index = index;
        this.label = label;
    }

    public static SharedUploadFolder getSharedUploadFolder(short index) {
        switch (index) {
            case 0:
                return MARS_SHARED_FOLDER;
            case 1:
                return AVESINA_SHARED_FOLDER;
            case -1:
                return null;
            default:
                throw new IllegalArgumentException("Only 0 or 1 is possible arguments.");
        }
    }

    public short getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    public static SharedUploadFolder getSharedUploadFolder(String label) {
        switch (label) {
            case "Mars":
                return MARS_SHARED_FOLDER;
            case "Avesina":
                return AVESINA_SHARED_FOLDER;
            default:
                throw new IllegalArgumentException();
        }
    }
}
