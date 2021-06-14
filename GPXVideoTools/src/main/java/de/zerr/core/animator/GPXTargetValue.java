package de.zerr.core.animator;

public enum GPXTargetValue {
	TIME, ACCELERATION, DISTANCE, ELEVATION, HEADING, LAT, LON, SLOPE, SPEED;

	public static final GPXTargetValue ALL_SET[] = new GPXTargetValue[] { TIME, SPEED, ELEVATION, LAT, LON, DISTANCE,
			SLOPE, HEADING, ACCELERATION };
	public static final GPXTargetValue ALLGPX_SET[] = new GPXTargetValue[] { TIME, SPEED, ELEVATION, LAT, LON };
	public static final GPXTargetValue EXTENDE_SET[] = new GPXTargetValue[] { DISTANCE, SLOPE, HEADING, ACCELERATION };
	public static final GPXTargetValue MANDATORY_SET[] = new GPXTargetValue[] { LAT, LON };
	public static final GPXTargetValue OPTIONALGPX_SET[] = new GPXTargetValue[] { SPEED, ELEVATION };
	public static final GPXTargetValue ALL_DEPENDENTSET[] = new GPXTargetValue[] {SPEED, ELEVATION, LAT, LON, DISTANCE,
			SLOPE, HEADING, ACCELERATION };
}
