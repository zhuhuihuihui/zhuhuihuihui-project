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
    birthday     | string        | Optional            | Must be in `mm/dd/yyyy`format, otherwise will be put `NULL`
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