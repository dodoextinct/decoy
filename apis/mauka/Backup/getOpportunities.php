<?php
    include  'connect_to_db.php';

    $user_id = $_POST["id"];
    $page = $_POST["page"]*15;
    $tags = $_POST["tags"];
    $language_id = $_POST["language_id"];
    $tags = json_decode($tags, TRUE);

    $statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS api_deleted (id INT AUTO_INCREMENT, user_id INT NOT NULL, event_id INT NOT NULL, PRIMARY KEY(id));");
    $statement->execute();
    $statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS api_languages (id INT AUTO_INCREMENT, languageDetails VARCHAR(255), PRIMARY KEY(languageDetails), KEY(id));");
    $statement->execute();
    $statement = $pdo->prepare("SELECT languageDetails FROM api_languages WHERE id = ?");
    $statement->execute([$language_id]);
    $data = $statement->fetch(PDO::FETCH_ASSOC);
    $languageDetails = $data['languageDetails'];

    $part_of_query = "";

    foreach ($tags as $value) {
        if (strlen($part_of_query)>0) {
            $part_of_query .= "OR TAGS LIKE '%".$value."%' ";
        }else{
            $part_of_query .= "(TAGS LIKE '%".$value."%' ";
        }
    }

    $query = "SELECT * FROM opportunities_".$languageDetails." WHERE DEADLINE >= CURDATE() AND ".$part_of_query.") AND ID NOT IN (SELECT event_id FROM api_deleted WHERE user_id = '$user_id') LIMIT $page, 15;";
    
    $statement = $pdo->prepare($query);
    $statement->execute();

    $spreadsheet_data = $statement->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($spreadsheet_data);
?>