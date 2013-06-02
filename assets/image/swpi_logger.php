<?php



require("config.php");

$swpipwd1 = $_POST['pwd'];

if ($swpipwd1 != $swpipwd)
{
  die('Wrong password '.$pwd.' '.$swpipwd);
}
  
$last_measure_time = $_POST['last_measure_time'];
$idx = $_POST['idx'];
$wind_dir_code = $_POST['wind_dir_code'];
$wind_dir = $_POST['wind_dir'];
$wind_ave = $_POST['wind_ave'];
$wind_gust = $_POST['wind_gust'];
$temp_out = $_POST['temp_out'];
$hum_out = $_POST['hum_out'];
$abs_pressure = $_POST['abs_pressure'];
$rain = $_POST['rain'];
$rain_rate = $_POST['rain_rate'];
$temp_in = $_POST['temp_in'];
$hum_in = $_POST['hum_in'];
$wind_chill = $_POST['wind_chill'];
$temp_apparent = $_POST['temp_apparent'];
$dew_point = $_POST['dew_point'];
$uv = $_POST['uv'];
$illuminance = $_POST['illuminance'];
$winDayMin = $_POST['winDayMin'];
$winDayMax = $_POST['winDayMax'];

$winDayGustMin  = $_POST['winDayGustMin'];  
$winDayGustMax = $_POST['winDayGustMax'];  
$TempOutMin = $_POST['TempOutMin'];  
$TempOutMax = $_POST['TempOutMax'];  
$TempInMin = $_POST['TempInMin'];  
$TempInMax = $_POST['TempInMax'];  
$UmOutMin = $_POST['UmOutMin'];  
$UmOutMax = $_POST['UmOutMax'];  
$UmInMin = $_POST['UmInMin'];  
$UmInMax = $_POST['UmInMax'];  
$PressureMin = $_POST['PressureMin'];  
$PressureMax = $_POST['PressureMax'];  
$wind_dir_ave = $_POST['wind_dir_ave'];  



// -- --------------------------------------------------------

// --
// -- Table structure for table `meteo`
// --

// CREATE TABLE IF NOT EXISTS `METEO` (
  // `TIMESTAMP_LOCAL` datetime NOT NULL,
  // `TIMESTAMP_IDX` datetime default NULL,
  // `WINDIR_CODE` text,
  // `WIND_DIR` smallint(6) default NULL,
  // `WIND_AVE` double default NULL,
  // `WIND_GUST` double default NULL,
  // `TEMP` double default NULL,
  // `PRESSURE` double default NULL,
  // `HUM` double default NULL,
  // `RAIN` double default NULL,
  // `RAIN_RATE` double default NULL,
  // `TEMPINT` double default NULL,
  // `HUMINT` double default NULL,
  // `WIND_CHILL` double default NULL,
  // `TEMP_APPARENT` double default NULL,
  // `DEW_POINT` double default NULL,
  // `UV_INDEX` double default NULL,
  // `SOLAR_RAD` double default NULL,
  // `WIND_DAY_MIN` double default NULL,
  // `WIND_DAY_MAX` double default NULL,
  // `WIND_DAY_GUST_MIN` double default NULL,
  // `WIND_DAY_GUST_MAX` double default NULL,
  // `TEMP_OUT_DAY_MIN` double default NULL,
  // `TEMP_OUT_DAY_MAX` double default NULL,
  // `TEMP_IN_DAY_MIN` double default NULL,
  // `TEMP_IN_DAY_MAX` double default NULL,
  // `HUM_OUT_DAY_MIN` double default NULL,
  // `HUM_OUT_DAY_MAX` double default NULL,
  // `HUM_IN_DAY_MIN` double default NULL,
  // `HUM_IN_DAY_MAX` double default NULL,
  // `PRESSURE_DAY_MIN` double default NULL,
  // `PRESSURE_DAY_MAX` double default NULL,
  // `WIND_DIR_AVE` double default NULL
// ) ENGINE=MyISAM DEFAULT CHARSET=latin1;


$con = mysql_connect($server,$user,$pwd);
mysql_select_db($db); 


if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("test", $con);


$sql = "INSERT INTO METEO (TIMESTAMP_LOCAL, TIMESTAMP_IDX, WINDIR_CODE, WIND_DIR, WIND_AVE, WIND_GUST, TEMP, PRESSURE, HUM, RAIN, RAIN_RATE, TEMPINT, HUMINT, WIND_CHILL, TEMP_APPARENT, DEW_POINT, UV_INDEX, SOLAR_RAD, WIND_DAY_MIN, WIND_DAY_MAX,WIND_DAY_GUST_MIN ,WIND_DAY_GUST_MAX ,TEMP_OUT_DAY_MIN ,TEMP_OUT_DAY_MAX,TEMP_IN_DAY_MIN ,TEMP_IN_DAY_MAX ,HUM_OUT_DAY_MIN ,HUM_OUT_DAY_MAX ,HUM_IN_DAY_MIN ,HUM_IN_DAY_MAX ,PRESSURE_DAY_MIN ,PRESSURE_DAY_MAX,WIND_DIR_AVE) VALUES ('".$last_measure_time."','".$idx."','".$wind_dir_code."',".$wind_dir.",".$wind_ave.",".$wind_gust.",".$temp_out.",".$abs_pressure.",".$hum_out.",".$rain.",".$rain_rate.",".$temp_in.",".$hum_in.",".$wind_chill.",".$temp_apparent.",".$dew_point.",".$uv.",".$illuminance.",".$winDayMin.",".$winDayMax.",".$winDayGustMin.",".$winDayGustMax.",".$TempOutMin.",".$TempOutMax.",".$TempInMin.",".$TempInMax.",".$UmOutMin.",".$UmOutMax.",".$UmInMin.",".$UmInMax.",".$PressureMin.",".$PressureMax.",".$wind_dir_ave." )";


$result = mysql_query($sql) ; 

if (!$result) {
	die("Errore nella query $query: " . mysql_error());
	//die("Errore nella query $query: " . $sql);

	}

mysql_close($con);

echo 'OK';

?>
