package se.vgregion.portal.wwwprv.service;

/**
 * @author Patrik Bergström
 */
public class LiferayServiceException extends Throwable {

    public LiferayServiceException(String message) {
        super(message);
    }

    /**
     * Redundant method only created for explicitness.
     *
     * @return Same as {@link Throwable#getMessage()}
     */
    @Override
    public String getLocalizedMessage() {
        return super.getMessage();
    }
}
