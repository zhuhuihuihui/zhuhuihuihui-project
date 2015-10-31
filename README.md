#API Documentation


---

##USER

* Login

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