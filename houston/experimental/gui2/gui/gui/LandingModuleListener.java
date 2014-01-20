package gui;

public interface LandingModuleListener {
	public void positionUpdated(Position newPosition, LandingModule m);
	public void dataUpdated(TPData currentData, LandingModule m);
	public void statusUpdated(LandingModule.DeploymentStatus deployed, boolean connected, LandingModule m);
}
