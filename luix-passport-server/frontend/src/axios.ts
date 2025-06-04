import axios from "axios"

const TIMEOUT = 60 * 1000

const instance = axios.create({
  paramsSerializer: { indexes: null }
})

instance.interceptors.request.use(
  function (config) {
    // Modify request config before sending
    config.headers["X-Trace-Id"] = "R" + Date.now() + (Math.random() * 100000).toFixed()
    config.timeout = TIMEOUT
    return config
  },
  function (error) {
    // Handle request errors
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  function (response) {
    // Modify response data before resolving the promise
    if(response.request.responseURL.indexOf("/login") !== -1) {
      // Redirect to login page
      window.location.href = "/sign-in"
    }
    return response
  },
  function (error) {
    // Handle response errors
    if (error.response.status === 403) {
      // Handle 403 forbidden error
      window.location.href = "/403"
    } else if (error.response.status === 401) {
      // Handle 401 unauthorized error
      console.log("Redirecting to login")
      window.location.href = "/sign-in"
    } else {
      // Handle other errors
      console.error("Request failed: ", error.message)
    }
    return Promise.reject(error)
  }
)

const http = instance

export default http
