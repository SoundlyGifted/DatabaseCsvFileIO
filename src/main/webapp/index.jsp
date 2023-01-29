<%@page import="java.io.*, java.util.*, java.sql.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix = "sql"%>

<!DOCTYPE html>
<html lang="en" class="js"> 
    <!-- JSTL database connection -->
    <sql:setDataSource driver="org.apache.derby.jdbc.ClientDriver"
                       url="jdbc:derby://localhost:1527/DatabaseCsvFileIOAppDB"
                       user="app"
                       password="app"
                       var="JSTLDBConnection"/>
    
    <!-- JSTL get all data from MYDATA table -->
    <sql:query dataSource="${JSTLDBConnection}" var="MYDATAResultSet">
        SELECT * FROM MYDATA
    </sql:query>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Web App to read/write csv-file from/to a database</title>
        
        <link rel="stylesheet" type="text/css" href="css/generalStyles.css">
        <link rel="stylesheet" type="text/css" href="css/inputFileStyle.css">

        <!-- Script for the case when JavaScript is not available (it will be
        not possible to indicate the selected file(s).
        This script replaces ".no-js" class with ".js" so that JS makes the 
        indication possible, otherwise the default look will be left for the 
        sake of usability. -->
        <script>
            (function(e,t,n) {
                var r=e.querySelectorAll("html")[0];
                r.className=r.className.replace(/(^|\s)no-js(\s|$)/,"$1js$2");
            })
            (document,window,0);
        </script>
        
    </head>
    
    <body>
        <div>
            <h2 style="color: #cccccc">Database Upload / Download csv-file [Example]</h2>
            <hr style="border-color: #0082a4;">            
        </div>

        <br/>
        
        <!-- Data upload/download block. -->
        <div>
            <!-- "process.do" is a url pattern for the processing servlet. 
            Can be defined in web.xml deployement descriptor of in @WebServlet 
            servlet annotation. -->
            <form method="post" action="process.do" enctype="multipart/form-data">
                <div class="div-in-select">
                    <select name = "selected_method" class="inputSelectBox">
                        <option value="" 
                                selected disabled hidden>
                            Choose Library
                        </option>                            
                        <option value="CommonsCSV">
                            Apache Commons CSV
                        </option>
                        <option value="OpenCSV">
                            Open CSV
                        </option>
                    </select>                    
                </div>

                <div class="div-in-upload">
                    <p style="font-size: 0.8rem; font-weight: bold;">Upload csv data to database</p>
                    <br/>
                    <input type="file" name="file" id="file-1" class="inputfile inputfile-1" data-multiple-caption="{count} files selected" multiple="">                    
                    <label for="file-1">
                        <span>Select csv ...</span>
                    </label>
                    <img src="img/arrow.ico" style="width: 1.5rem; height: 1.5rem; display:inline-block; margin-right: 0.625rem; vertical-align: middle;">
                    <input type="submit" name="clicked_Upload" value="Upload" class="button"/>                    
                </div>

                <div class="div-in-download">
                    <p style="font-size: 0.8rem; font-weight: bold;">Download database data to csv</p>
                    <br/>
                    <input type="submit" name="clicked_Download" value="Download" class="button"/>
                </div>
            </form>
            
            <div class="div-out-message">
            <!-- JSTL code to display the result of operation received as 
            http request attribute -->
                <c:if test="${operationResultDesc == null}">
                    <h4>No operation performed yet ...</h4>
                </c:if>
                <h4><c:out value="${operationResultDesc}"></c:out></h4>  
            </div>
        </div>
        
        <!-- Database MYDATA table display. -->
        <div class="div-out-table">
            <table class="outputTable">
                <caption> 
                    <form method="post" action="process.do">
                        <h4 style="display:inline-block; margin-right: 0.625rem; vertical-align: middle;">Players points [Database table]</h4>
                        <input type="submit" name="clicked_Clear" value="Clear data" class="button"/>
                    </form>
                </caption>
                <tr style="background-color: cadetblue">
                    <th>Record [ID]</th>
                    <th>Player Name [Text]</th>
                    <th>Score [Double]</th>
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
