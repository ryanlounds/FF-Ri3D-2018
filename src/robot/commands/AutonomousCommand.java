package robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import robot.commands.drive.AccelerateDistanceCommand;
import robot.commands.drive.ArcCommand;
import robot.commands.drive.BackupCommand;
import robot.commands.drive.DriveDistanceCommand;
import robot.commands.drive.DriveToCubeCommand;
import robot.commands.drive.RotateToAngleCommand;
import robot.commands.elevator.SetElevatorHeightCommand;
import robot.commands.intake.AutoCubeReleaseCommand;
import robot.commands.intake.AutomaticIntakeCommand;
import robot.oi.AutoSelector;
import robot.oi.GameData;

/**
 *
 */
public class AutonomousCommand extends CommandGroup {

	public static final char LEFT 				= 'L';
	public static final char RIGHT 				= 'R';
	public static final char CENTER 			= 'C';
	public static final String ROBOT_LEFT   	= "Robot Left";
	public static final String ROBOT_CENTER 	= "Robot Center";
	public static final String ROBOT_RIGHT  	= "Robot Right";
	public static final String SWITCH 			= "Switch";
	public static final String SCALE 			= "Scale";
	public static final String CROSS  			= "Cross";
	public static final String CUBE 			= "Cube";
	public static final String CLOSE_SCALE_ONLY = "Close Scale";
	public static final String NONE  			= "None";
	public static final String EXCHANGE = "Exchange";

	public AutonomousCommand() {
		//getting info
		String robotStartPosition 	= AutoSelector.getRobotStartPosition();
		String firstAction 			= AutoSelector.getRobotFirstAction();
		String secondAction 		= AutoSelector.getRobotSecondAction();
		char closeSwitch 			= GameData.getCloseSwitch();
		char scale 					= GameData.getScale();


		// Print out the user selection and Game config for debug later
		//		System.out.println("Auto Command Configuration");
		//		System.out.println("--------------------------");
		//		System.out.println("Robot Position : " + robotStartPosition);
		//		System.out.println("First Action   : " + firstAction);
		//		System.out.println("Second Action  : " + secondAction);
		//		System.out.println("Close Switch   : " + closeSwitch);
		//		System.out.println("Scale		   : " + scale);

		//overrides
		//System.out.println("Starting Overrides");

		if (robotStartPosition.equals(ROBOT_CENTER) && !firstAction.equals(SWITCH)) {
			firstAction = SWITCH;
			System.out.println("Center start must do switch as first action. Overriding first action to SWITCH");
		}

		//run the auto

		switch (robotStartPosition) {
		case ROBOT_LEFT:
			leftAuto(scale, closeSwitch, firstAction, secondAction);
			break;
		case ROBOT_CENTER:
			centerAuto(scale, closeSwitch, firstAction, secondAction);
			break;
		case ROBOT_RIGHT:
			rightAuto(scale, closeSwitch, firstAction, secondAction);
			break;
		}

	}

	private void leftAuto(char scale, char closeSwitch, String firstAction, String secondAction) {

		//first action
		if (firstAction.equals(SCALE)) {
			//scale action is selected
			if (scale == LEFT) {
				//scale is on the left side
				leftScaleLeft1();
			}
			else{
				//System.out.println("scale is on the right side");
				leftScaleRight1();
			}
		}
		else if (firstAction.equals(CLOSE_SCALE_ONLY)) {
			if (scale == LEFT) {
				//scale is on the left side
				leftScaleLeft1();
			}
			else {
				crossLine();
			}
		}
		else{
			//cross action is selected
			crossLine();
		}
		//System.out.println("Starting second action");
		if (secondAction.equals(SWITCH)) {
			//System.out.println("Switch action selected");
			if (closeSwitch == LEFT){
				if (scale == LEFT) {
					leftSwitchLeft2();
				}
			}
		}

		else if (secondAction.equals(SCALE)) {
			if (scale == LEFT){
				if (firstAction.equals(SCALE) || firstAction.equals(CLOSE_SCALE_ONLY)) {
					leftScaleLeft2();
				}
			}
			else {
				if (firstAction.equals(SCALE)) {
					leftScaleRight2();
				}
			}
		}

		else{
			System.out.println("No second action");
		}
	}

	private void rightAuto(char scale, char closeSwitch, String firstAction, String secondAction) {

		//first action
		if (firstAction.equals(SCALE)) {
			//scale action is selected

			if (scale == RIGHT){
				//scale is on the right side
				rightScaleRight1();
			}
			else{
				//scale is on the left side
				rightScaleLeft1();
			}
		}
		else if (firstAction.equals(CLOSE_SCALE_ONLY)) {
			if (scale == RIGHT) {
				//scale is on the left side
				rightScaleRight1();
			}
			else {
				crossLine();
			}
		}
		else{
			//cross action is selected
			crossLine();
		}

		//System.out.println("Starting second action");
		if (secondAction.equals(SWITCH)) {
			//System.out.println("Switch action selected");
			if (closeSwitch == RIGHT) {
				if (firstAction.equals(SCALE)) {
					if (scale == RIGHT) {
						rightSwitchRight2();
					}
				}
			}
		}
		else if (secondAction.equals(SCALE) || firstAction.equals(CLOSE_SCALE_ONLY)) {
			if (scale == RIGHT){
				rightScaleRight2();
			}
			else {
				rightScaleLeft2();
			}
		}
		else{
			//System.out.println("No second action");
		}
	}

	private void centerAuto(char scale, char closeSwitch, String firstAction, String secondAction) {
		//System.out.println("Starting first action");

		//System.out.println("Switch action selected");
		if (closeSwitch == LEFT){
			//System.out.println("Executing left switch command");
			centerSwitchLeft1();
		}
		else{
			//System.out.println("Executing right switch command");	
			centerSwitchRight1();
		}

		//System.out.println("Starting second action");
		if (secondAction.equals(CUBE)) {
			if (closeSwitch == LEFT) {
				leftGetPowerCube();

			}
			else {
				rightGetPowerCube();
			}
		} else if (secondAction.equals(EXCHANGE)) {

		}
	}

	//first action methods 
	// pattern methods name = (Start Position*)(Destination*)(Side)(Action Number)
	// * = mandatory name parameter

	//left side start

	private void leftScaleLeft1(){
		addParallel(new SetElevatorHeightCommand(3));
		addSequential(new AccelerateDistanceCommand(200, 0, 1.0, 5.0, false));
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new ArcCommand(100, 0, 40, 0.45, true));
		addSequential(new AutoCubeReleaseCommand());
		addSequential(new BackupCommand(10));
	}

	private void leftScaleRight1(){
		addParallel(new SetElevatorHeightCommand(1));
		addSequential(new AccelerateDistanceCommand(180, 0, 1.0, 3.0, false));
		addSequential(new ArcCommand(100, 0, 90, 0.8, false));
		addParallel(new SetElevatorHeightCommand(1));
		addSequential(new DriveDistanceCommand(130, 90, 1.0, 5.0, false));
		addParallel(new DriveDistanceCommand(10, 90, 0.2, 5.0, false));
		addSequential(new SetElevatorHeightCommand(5));
		addSequential(new ArcCommand(90, 90, 350, 0.4, true));
		addSequential(new AutoCubeReleaseCommand());
		addSequential(new BackupCommand(10));
	}

	//right side start
	private void rightScaleLeft1(){
		addParallel(new SetElevatorHeightCommand(1));
		addSequential(new AccelerateDistanceCommand(190, 0, 1.0, 3.0, false));
		addSequential(new ArcCommand(100, 0, 275, 0.8, false));
		addParallel(new SetElevatorHeightCommand(1));
		addSequential(new DriveDistanceCommand(130, 270, 1.0, 5.0, false));
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new ArcCommand(90, 270, 10, 0.6, true));
		addSequential(new AutoCubeReleaseCommand());
		addSequential(new BackupCommand(10));
	}

	private void rightScaleRight1(){
		addParallel(new SetElevatorHeightCommand(3));
		addSequential(new AccelerateDistanceCommand(210, 0, 1.0, 5.0, false));
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new ArcCommand(100, 0, 320, 0.45, true));
		addSequential(new AutoCubeReleaseCommand());
		addSequential(new BackupCommand(10));
	}

	//center start
	private void centerSwitchLeft1(){
		addParallel(new SetElevatorHeightCommand(2));
		addSequential(new ArcCommand(85, 0, 310, 0.8, false));
		addSequential(new ArcCommand(85, 310, 0, 0.8, false));
		addSequential(new DriveDistanceCommand(20, 0, 0.8, 7.0, false));
		addSequential(new AutoCubeReleaseCommand());
	}
	private void centerSwitchRight1(){
		addParallel(new SetElevatorHeightCommand(2));
		addSequential(new ArcCommand(80, 0, 45,0.8, false));
		addSequential(new DriveDistanceCommand(3, 45, 0.8, 3.0, false));
		addSequential(new ArcCommand(110, 45, 0, 0.8, false));
		addSequential(new DriveDistanceCommand(20, 0, 0.8, 7.0, false));
		addSequential(new AutoCubeReleaseCommand());
	}

	//universal
	private void crossLine(){
		addSequential(new DriveDistanceCommand(200, 0, 1.0, 3.0, false));
	}

	//second action methods
	// pattern methods name = (Side going into second action*)(Destination*)(Side)(Action Number)
	// * = mandatory name parameter

	//left side
	public void leftSwitchRight2(){
		leftScaleLeft2();
				addSequential(new ArcCommand(100, 0, 80, 1.0, false));
				addSequential(new DriveDistanceCommand(180, 80, 1.0, 5.0, false));
				addSequential(new ArcCommand(100, 0, 170, 1.0, true));
				addSequential(new AutoCubeReleaseCommand());
	}
	public void leftSwitchLeft2(){
		addSequential(new SetElevatorHeightCommand(2));
		addSequential(new DriveDistanceCommand(10, 150, 0.3, 7.0, false));
		addSequential(new AutoCubeReleaseCommand());
	}
	public void leftScaleRight2(){
		// Go for second cube
		addParallel(new SetElevatorHeightCommand(0));
		addSequential(new RotateToAngleCommand(210, 0.3));
		addSequential(new SetElevatorHeightCommand(0));
		addParallel(new DriveToCubeCommand(60, 200, 0.7, 2.0));
		addSequential(new AutomaticIntakeCommand());

		// Re-intake when backing up
		addSequential(new RotateToAngleCommand(210, 0.6));
		addParallel(new AutomaticIntakeCommand());
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new BackupCommand(60, .8));
		addSequential(new SetElevatorHeightCommand(5));
		addSequential(new RotateToAngleCommand(300, 0.3));
		addSequential(new AutoCubeReleaseCommand());

		// Back up and put the arm down
		addSequential(new BackupCommand(10, .5));
		addSequential(new SetElevatorHeightCommand(0));
	}
	public void leftScaleLeft2(){
		// Go for second cube
		addParallel(new SetElevatorHeightCommand(0));
		addSequential(new RotateToAngleCommand(90, 0.3));
		addSequential(new SetElevatorHeightCommand(0));
		addParallel(new DriveToCubeCommand(80, 140, 0.6, 3.0));
		addSequential(new AutomaticIntakeCommand());
		
		// Re-intake when backing up
		addParallel(new AutomaticIntakeCommand());
		addParallel(new SetElevatorHeightCommand(1));
		addSequential(new BackupCommand(5, .3));
		addSequential(new RotateToAngleCommand(160, 0.3));
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new BackupCommand(65, .7));
		addSequential(new SetElevatorHeightCommand(5));
		addSequential(new RotateToAngleCommand(70, 0.3
				));
//		addSequential(new ArcCommand(150, 15, 350, 0.4, true));
		addSequential(new AutoCubeReleaseCommand());
		
		// Back up and put the arm down
		addParallel(new BackupCommand(10, .5));
		addSequential(new SetElevatorHeightCommand(0));

	}

	//right side
	public void rightSwitchRight2(){
		addSequential(new SetElevatorHeightCommand(2));
		addSequential(new RotateToAngleCommand(200, 0.5));
		addSequential(new DriveDistanceCommand(10, 200, 0.3, 7.0, false));
		addSequential(new AutoCubeReleaseCommand());
	}
	public void rightSwitchLeft2(){
		rightScaleRight2();
		//		addSequential(new ArcCommand(100, 0, 280, 1.0, false));
		//		addSequential(new DriveDistanceCommand(180, 80, 1.0, 5.0, false));
		//		addSequential(new ArcCommand(100, 0, 190, 1.0, true));
		//		addSequential(new AutoCubeReleaseCommand());
	}
	public void rightScaleRight2(){	
		// Go for second cube
		addParallel(new SetElevatorHeightCommand(0));
		addSequential(new RotateToAngleCommand(270, 0.6));
		addSequential(new SetElevatorHeightCommand(0));
		addParallel(new DriveToCubeCommand(60, 220, 0.7, 7.0));
		addSequential(new AutomaticIntakeCommand());
		// Re-intake when backing up
		addParallel(new AutomaticIntakeCommand());
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new BackupCommand(60, .7));
		addSequential(new SetElevatorHeightCommand(4));
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new RotateToAngleCommand(290, 0.4
				));
//		addSequential(new ArcCommand(150, 15, 350, 0.4, true));
		addSequential(new AutoCubeReleaseCommand());
		
		// Back up and put the arm down
		addParallel(new BackupCommand(10, .5));
		addSequential(new SetElevatorHeightCommand(0));

		// Go for third cube
//		addParallel(new IntakeRotatetoAngleCommand(0));
//		addSequential(new BackupCommand(10, .3));
//		addParallel(new SetElevatorHeightCommand(0));
//		addSequential(new RotateToAngleCommand(290, 0.5));
//		addParallel(new SetElevatorHeightCommand(0));
//		addParallel(new DriveToCubeCommand(1200, 210, 0.7, 7.0));
//		addSequential(new AutomaticIntakeCommand());
//		// Re-intake when backing up
//		addParallel(new AutomaticIntakeCommand());
//		addParallel(new SetElevatorHeightCommand(5));
//		addSequential(new BackupCommand(60, .7));
//		addParallel(new SetElevatorHeightCommand(5));
//		addParallel(new IntakeRotatetoAngleCommand(15));
//		addSequential(new RotateToAngleCommand(290, 0.4
//				));
//		//		addSequential(new ArcCommand(150, 15, 350, 0.4, true));
//		addSequential(new AutoCubeReleaseCommand());
	}
	public void rightScaleLeft2(){

		// Go for second cube
		addParallel(new SetElevatorHeightCommand(0));
		addSequential(new RotateToAngleCommand(150, 0.3));
		addSequential(new SetElevatorHeightCommand(0));
		addParallel(new DriveToCubeCommand(60, 160, 0.7, 2.0));
		addSequential(new AutomaticIntakeCommand());

		// Re-intake when backing up
		addSequential(new RotateToAngleCommand(150, 0.6));
		addParallel(new AutomaticIntakeCommand());
		addParallel(new SetElevatorHeightCommand(5));
		addSequential(new BackupCommand(60, .8));
		addSequential(new SetElevatorHeightCommand(5));
		addSequential(new RotateToAngleCommand(60, 0.3));
		addSequential(new AutoCubeReleaseCommand());

		// Back up and put the arm down
		addSequential(new BackupCommand(20, .5));
		addSequential(new SetElevatorHeightCommand(0));
	}

	//Center
	public void leftGetPowerCube() {
		addSequential(new BackupCommand(10));
		addSequential(new SetElevatorHeightCommand(0));
		addSequential(new RotateToAngleCommand(80, 0.5));
		addParallel(new AutomaticIntakeCommand());
		addSequential(new ArcCommand(60, 90, 70, 0.5, false));
	}
	public void rightGetPowerCube() {
		addSequential(new BackupCommand(10));
		addSequential(new SetElevatorHeightCommand(0));
		addSequential(new RotateToAngleCommand(280, 0.5));
		addParallel(new AutomaticIntakeCommand());
		addSequential(new ArcCommand(60, 270, 290, 0.5, false));
	}
}
