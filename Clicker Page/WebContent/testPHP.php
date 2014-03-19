<?php
	$oldVotes = file_get_contents("answers.txt");
	//$oldVotes = "1 2 3 4 5";
	$f = fopen("answers.txt", "w+");

	//$answers = $_POST["answers"];
    $answers = "1 1 2 2 3";
	$oldAbcd = explode(" ", $oldVotes); //the old ones 
	$newAbcd = explode(" ", $answers); //the new ones 

	$zeros = True;
	for($i=0; $i<5; $i++){
		echo $newAbcd[$i];
		if($newAbcd[$i] != 0){
			$zeros = false;
		}
	}

	//check to see if any of the new votes went up or if they were reset
	if($zeros){
		$oldAbcd = $newAbcd;
	}
	else{
		for($i=0; $i < 5; $i++){
			if($newAbcd[$i] > 0){
				$oldAbcd[$i] += 1;
			}
		}	
	}
	
	$answers = implode(" ", $oldAbcd);
	fwrite($f, $answers);
	fclose($f);
?>