<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Lucene Poc</title>
</head>
<body>

<h3>Poc do Lucene</h3>

<br />
<form action="/indexar" method="post">
Indexar Texto: <input type="text" name="texto" />
<input type="submit" value="Indexar" /> 
</form>

<form action="/procurar"  method="post">
Buscar Texto: <input type="text" name="parametro" />
<input type="submit" value="Buscar" /> 
</form>

</body>
</html>