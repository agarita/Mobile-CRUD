<?php
  /*
  https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/
  This is our API, we will send request to this file only from the android side. And this file will handle all the API calls.
  */

  //getting the dboperation class
	require_once '../includes/DbOperation.php';

	//function validating all the paramters are available
	//we will pass the required parameters to this function
	function isTheseParametersAvailable($params){
		//assuming all parameters are available
		$available = true;
		$missingparams = "";

		foreach($params as $param){
			if(!isset($_POST[$param]) || strlen($_POST[$param])<=0){
				$available = false;
				$missingparams = $missingparams . ", " . $param;
			}
		}

		//if parameters are missing
		if(!$available){
			$response = array();
			$response['error'] = true;
			$response['message'] = 'Parameters ' . substr($missingparams, 1, strlen($missingparams)) . ' missing';

			//displaying error
			echo json_encode($response);

			//stopping further execution
			die();
		}
	}

	//an array to display response
	$response = array();

	//if it is an api call
	//that means a get parameter named api call is set in the URL
	//and with this parameter we are concluding that it is an api call
	if(isset($_GET['apicall'])){

		switch($_GET['apicall']){

			//the CREATE operation
			case 'createPerson':
				//first check the parameters required for this request are available or not
				isTheseParametersAvailable(array('name','last_name'));

				//creating a new dboperation object
				$db = new DbOperation();

				//creating a new record in the database
				$result = $db->createPerson(
					$_POST['name'],
					$_POST['last_name']
				);


				//if the record is created adding success to response
				if($result){
					//record is created means there is no error
					$response['error'] = false;

					//in message we have a success message
					$response['message'] = 'Person addedd successfully';

					//and we are getting all the heroes from the database in the response
					$response['persons'] = $db->getPersons();
				}else{

					//if record is not added that means there is an error
					$response['error'] = true;

					//and we have the error message
					$response['message'] = 'Some error occurred please try again';
				}
        break;

			//the READ operation
			case 'getPersons':
				$db = new DbOperation();
				$response['error'] = false;
				$response['message'] = 'Request successfully completed';
				$response['persons'] = $db->getPersons();
			  break;

			//the UPDATE operation
			case 'updatePerson':
				isTheseParametersAvailable(array('id','name','last_name'));
				$db = new DbOperation();
				$result = $db->updatePerson(
					$_POST['id'],
					$_POST['name'],
					$_POST['last_name']
				);

				if($result){
					$response['error'] = false;
					$response['message'] = 'Person updated successfully';
					$response['persons'] = $db->getPersons();
				}else{
					$response['error'] = true;
					$response['message'] = 'Some error occurred please try again';
				}
			  break;

			//the DELETE operation
			case 'deletePerson':

				//for the delete operation we are getting a GET parameter from the url having the id of the record to be deleted
				if(isset($_GET['id'])){
					$db = new DbOperation();
					if($db->deletePerson($_GET['id'])){
						$response['error'] = false;
						$response['message'] = 'Person deleted successfully';
						$response['persons'] = $db->getPersons();
					}else{
						$response['error'] = true;
						$response['message'] = 'Some error occurred please try again';
					}
				}else{
					$response['error'] = true;
					$response['message'] = 'Nothing to delete, provide an id please';
				}
			  break;
		}

	}else{
		//if it is not api call
		//pushing appropriate values to response array
		$response['error'] = true;
		$response['message'] = 'Invalid API Call';
	}

	//displaying the response in json structure
	echo json_encode($response);

?>
