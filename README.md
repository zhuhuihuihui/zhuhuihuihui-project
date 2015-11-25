#API Documentation


---

##USER

* Login

    *Request*
    `POST /login`
    
    Parameters   | Data Type     | Required / Optional | Description
    ------------ | ------------- | ------------------- | -----------
    email        | string        | Required            | your email address
    password     | string        | Required            | your password
    device       | string        | Required            | the device you are calling this API from
    
    *Response*
```json
    {
        "success": true,
        "token": "xxx"
    }
```

```json
    {
        "success": false,
        "error": "This user does not exist!"
    }
```

* Registration

    *Request*
    `POST /signup`
    
    Parameters   | Data Type     | Required / Optional | Description
    ------------ | ------------- | ------------------- | -----------
    email        | string        | Required            | your email address
    password     | string        | Required            | your password
    device       | string        | Required            | the device you are calling this API from
    nickname     | string        | Optional            | Your nickname
    birthday     | string        | Optional            | Must be in `YYYY-MM-DD`format, otherwise will be put `NULL`
    gender       | string        | Optional            | Must be either `Male`, `Female` or `Neutral`, otherwise will be put `NULL`
    fromCity     | string        | Optional            | Your hometown
    avatorUrl    | string        | Optional            | put avator link here
    
    *Response*
```json
    {
        "success": true,
        "token": "xxx"
    }
```

```json
    {
        "success": false,
        "error": "Error message showing that why it didn't sign up successfully"
    }
```

---

##Businesses

* Add Business

    *Request*
    `POST /business/add`
    
    Parameters   | Data Type     | Required / Optional | Description
    ------------ | ------------- | ------------------- | -----------
    token        | string        | Required            | this operation must be authorized
    name         | string        | Required            | name of your business
    city         | string        | Required            | city where your business located
    address      | string        | Required            | address of your business
    state        | string        | Optional            | state of your business
    description  | string        | Optional            | write something about your business
    
    *Response*
```json
    {
        "success": true,
        "id": "id of your business"
    }
```

```json
    {
        "success": false,
        "error": "reason why your request failed"
    }
```

* Get Business

    *Request*
    `POST /business/get`
    
    Parameters   | Data Type     | Required / Optional | Description
    ------------ | ------------- | ------------------- | -----------
    limit        | string        | Optional            | The number of businesses you want to fetch, if you're not specifying this param, it will be set to 10 by default, and it has a maximum of 50. 
    city         | string        | Optional            | city where you want to fetch businesses from
    id           | int           | Optional            | id of your business, if this param is specified, `city` `name` will be ignored
    name         | ??            | ???                 | ???
    
    *Response*
```json
    {
        "success": true,
        "business": [{
            "businessId": "",
        },{},{}]
    }
```

```json
    {
        "success": false,
        "error": "reason why your request failed"
    }
```
