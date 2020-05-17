<?php
  /*
  https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/
  The actual CRUD operation is performed inside this file.
  */

  class DbOperation
  {
    //Database connection link
    private $con;

    //Class constructor
    function __construct()
    {
      //Getting the DbConnect.php file
      require_once dirname(__FILE__) . '/DbConnect.php';

      //Creating a DbConnect object to connect to the database
      $db = new DbConnect();

      //Initializin out connection link to this class
      //by calling the method connect of DbConnect class
      $this->con = $db->connect();
    }

    //The create operation
    function createPerson($name, $last_name){
      $stmt = $this->con->prepare("INSERT INTO Person(name, last_name) VALUES (?,?)");
      $stmt->bind_param("ss", $name, $last_name);
      if($stmt->execute())
        return true;
      return false;
    }

    // The read operation
    function getPersons(){
      $stmt = $this->con->prepare("SELECT id, name, last_name FROM Person");
  		$stmt->execute();
  		$stmt->bind_result($id, $name, $last_name);

  		$persons = array();

  		while($stmt->fetch()){
  			$person  = array();
  			$person['id'] = $id;
  			$person['name'] = $name;
  			$person['last_name'] = $last_name;

        array_push($persons, $person);
  		}

  		return $persons;
    }

    // The update operation
    function updatePerson($id, $name, $last_name){
      $stmt = $this->con->prepare("UPDATE Person SET name = ?, last_name = ? WHERE id = ?");
  		$stmt->bind_param("ssi", $name, $last_name, $id);
  		if($stmt->execute())
  			return true;
  		return false;
    }

    // The delete operation
    function deletePerson($id){
      $stmt = $this->con->prepare("DELETE FROM Person WHERE id = ? ");
      $stmt->bind_param("i", $id);
      if($stmt->execute())
        return true;
      return false;
    }
  }
?>
