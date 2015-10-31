#API Documentation


---

##USER

* Login

    *Request*
    
    `POST /login?email=youremail&password=yourpassword&device=theDeviceYouAreCallingThisAPIFrom`
    
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
    
    `POST /login?email=youremail&password=yourpassword&device=theDeviceYouAreCallingThisAPIFrom`
    
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

---