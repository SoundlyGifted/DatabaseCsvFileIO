<!-- Used sources:
1. Custom input file.
    https://tympanus.net/codrops/2015/09/15/styling-customizing-file-inputs-smart-way/
    Also check out
    https://dev.to/faddalibrahim/how-to-create-a-custom-file-upload-button-using-html-css-and-javascript-1c03
2. Uploading file to database. Multipart/form-data
    https://www.codejava.net/java-ee/servlet/java-file-upload-example-with-servlet-30-api
    https://www.codejava.net/coding/upload-files-to-database-servlet-jsp-mysql
    https://www.codejava.net/java-se/networking/upload-files-by-sending-multipart-request-programmatically
3. Apache Commons CSV
    https://commons.apache.org/proper/commons-csv/user-guide.html
    https://stackabuse.com/reading-and-writing-csvs-in-java-with-apache-commons-csv/
-->

<%@page import="java.io.*, java.util.*, java.sql.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix = "sql"%>

<!DOCTYPE html>
<html lang="en" class="js"> 
    <!-- JSTL database connection -->
    <sql:setDataSource driver="org.apache.derby.jdbc.ClientDriver"
                       url="jdbc:derby://localhost:1527/UploadFileToDBTestDB"
                       user="app"
                       password="app"
                       var="JSTLDBConnection"/>
    
    <!-- JSTL get all data from MYDATA table -->
    <sql:query dataSource="${JSTLDBConnection}" var="MYDATAResultSet">
        SELECT * FROM MYDATA
    </sql:query>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Database Upload/Download File Test Application</title>
        <link rel="stylesheet" type="text/css" href="css/inputFileStyle.css">
        <link rel="stylesheet" type="text/css" href="css/generalStyles.css">
        
        <script>
            (function(e,t,n) {
                var r=e.querySelectorAll("html")[0];
                r.className=r.className.replace(/(^|\s)no-js(\s|$)/,"$1js$2");
            })
            (document,window,0);
        </script>
        
    </head>
    
    <body>
        <h2>Database Upload/Download File Test Application</h2>
        <br/>
        <!-- Data upload/download block. -->
        <div>
            <form method="post" action="FileUploadServlet" enctype="multipart/form-data">
                Select file to upload:
                        <br/>
                        <br/>
                    <input type="file" name="file" id="file-1" class="inputfile inputfile-1" data-multiple-caption="{count} files selected" multiple="">                    
                    <label for="file-1">
                        <span>Click to select file ...</span>
                    </label>     

                        <br/>
                        <br/>
                    <input type="submit" name="UploadWithCommonsCSV" value="Upload (Apache Commons CSV Library)" class="button"/>
                    <input type="submit" name="UploadWithOpenCSV" value="Upload (Open CSV Library)" class="button"/>
                        <br/>
                        <br/>
            </form>
            <form method="post" action="FileDownloadServlet">
                Download data into a file:
                        <br/>
                        <br/>
                    <input type="submit" name="DownloadWithCommonsCSV" value="Download (Apache Commons CSV Library)" class="button"/>
                    <input type="submit" name="DownloadWithOpenCSV" value="Download (Open CSV Library)" class="button"/>
                        <br/>
                        <br/>
            </form>
        </div>
        
        <!-- Database MYDATA table display. -->
        <div>
            <table class="outputTable">
                <caption> MYDATA database table </caption>
                <tr style="background-color: cadetblue">
                    <th>ID</th>
                    <th>Text Data</th>
                    <th>Double Data</th>
                </tr>
                <c:forEach var="row" items="${MYDATAResultSet.rows}">
                    <tr>
                        <td> <c:out value="${row.ID}"/> </td>
                        <td> <c:out value="${row.TEXTDATA}"/> </td>
                        <td> <c:out value="${row.DOUBLEDATA}"/> </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        
        <script src="js/custom-file-input.js"></script>          
    </body>
    
</html>
