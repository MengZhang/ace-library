package org.agmip.ace;

public interface IAceBaseComponent {
    public String getId();
    public AceComponentType getType();
    public byte[] getRawComponent();
}
