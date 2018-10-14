package robot.commands.intake;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import robot.Robot;

/**
 *
 */
public class DefaultIntakeCommand extends Command {

	public DefaultIntakeCommand() {
		// Use requires() here to declare subsystem dependencies
		requires(Robot.intakeSubsystem);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {

		Robot.intakeSubsystem.intakeClawClose();
		// Open the forearms and start intaking once the forearm is open
		// if (Robot.oi.getIntakeForeArm() > 0.1) {
		// Robot.intakeSubsystem.openForeArms();
		//// Robot.intakeSubsystem.intakeCube(0.5);
		//
		// // Close the forearms once you choose to close it
		// } else if (Robot.oi.getOuttakeForeArm() > 0.1) {
		//
		// Robot.intakeSubsystem.closeForeArms();
		// }

	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {

		if (Robot.oi.reset()){
		}

		if (Robot.oi.getAutomaticIntake()) {
			Scheduler.getInstance().add(new TeleopAutomaticIntakeCommand());
		}

		if (Robot.oi.getClawOpen()) {
			Robot.intakeSubsystem.intakeClawOpen();
		} else if (Robot.oi.getClawOpen()&& Robot.intakeSubsystem.isCubeDetected()) {
			Robot.intakeSubsystem.intakeClawOpen();
			Robot.intakeSubsystem.outtakeCube();
		} else {
			Robot.intakeSubsystem.intakeClawClose();
		}


		
		if (Robot.oi.getIntakeCube()) {
			Robot.intakeSubsystem.intakeCube();
		} else if (Robot.oi.getOuttakeCube() > 0.1) {
			Robot.intakeSubsystem.outtakeCube(Robot.oi.getOuttakeCube());
		} else {
			Robot.intakeSubsystem.intakeStop();
		
		}

		// if the intake is jammed, then release it slightly and pull it back
		//		double voltage = 0.0;
		//
		//		if (!cubeJammed) {
		//			if (voltage > 1) {
		//				cubeJammed = true;
		//				intakeJammedTime = System.currentTimeMillis();
		//			}
		//		}
		//
		//		if (cubeJammed) {
		//			Robot.intakeSubsystem.outtakeCube();
		//			if (System.currentTimeMillis() - intakeJammedTime < 500) {
		//				Robot.intakeSubsystem.intakeCube();
		//				cubeJammed = false;
		//			}
		//		}

		// // Handle lift
		// if (Robot.oi.getLiftArmUp()) {
		// Robot.intakeSubsystem.tiltIntakeArmUp();
		// } else if (Robot.oi.getLiftArmDown()) {
		// Robot.intakeSubsystem.tiltIntakeArmDown();
		// }

		// If the intake motor is running and the cube is detected...
		// it means we are trying to intake cube
		// Close the clamp and stop the motors from running
		// Lift the arm up
		// if (Robot.intakeSubsystem.isCubeDetected() &&
		// Robot.intakeSubsystem.isIntakeMotorRunning()) {
		// Robot.intakeSubsystem.intakeClampClose();
		// Robot.intakeSubsystem.intakeStop();
		// Robot.intakeSubsystem.tiltIntakeArmUp();
		// }

	} 

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
	}
}
