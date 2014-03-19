<?php
	//get new and old votes in two strings
	$oldVotes = file_get_contents("answers.txt");
	$f = fopen("answers.txt", "w+");

	$answers = "0 0 0 0 1"; //$_POST["answers"];
	$oldAbcd = explode(" ", $oldVotes); //the old ones 
	$newAbcd = explode(" ", $answers); //the new ones 

    print_r($newAbcd);
	//check to see if any of the new votes went up or if they were reset
	$zeros = True;
	for($i=0; $i<5; $i++){
		if($newAbcd[$i] !=0){
			$zeros = False;
		}
	}

	if($zeros){
		$oldAbcd = $newAbcd;
	}
	else{
		for($i=0; $i < 5; $i++) {
			if($newAbcd[$i] > 0) {
				$oldAbcd[$i] += 1;
			}
		}
	}
	
	$answers = implode(" ", $oldAbcd);
	fwrite($f, $answers);
	fclose($f);
?>
