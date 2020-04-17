
## Create catalog

    curl -X POST --header 'Content-Type: application/json' --header 'Accept: text/plain' -d '{ \ 
       "catalogId": 10, \ 
       "name": "pippo" \ 
     }' 'http://localhost:8080/api/catalog'

## Add product

 
    curl -X POST --header 'Content-Type: application/json' --header 'Accept: text/plain' -d '{ \ 
       "label": "pippo", \ 
       "sku": "sku" \ 
     }' 'http://localhost:8080/api/library/10/product' 
 
## Get catalog 
 
    curl -X GET --header 'Accept: application/json' 'http://localhost:8080/api/library/10/product'
    
    