import axios, { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from "axios"

const TIMEOUT = 60 * 1000

const instance: AxiosInstance = axios.create({
  paramsSerializer: { indexes: null },
  withCredentials: true
})

instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Modify request config before sending
    config.headers["X-Trace-Id"] = "R" + Date.now() + (Math.random() * 100000).toFixed()
    config.timeout = TIMEOUT
    return config
  },
  (error) => {
    // Handle request errors
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response: AxiosResponse) => {
    // Modify response data before resolving the promise
    // Only redirect if the response is actually coming from the login page (not just containing the string)
    // const url = response.request?.responseURL
    // if (url && (url.endsWith("/login") || url.includes("/login?")) && !url.includes("/api/")) {
    //   // Redirect to login page
    //   console.warn("Detected redirect to login page in response URL: ", url)
    //   window.location.href = "/sign-in"
    // }
    return response
  },
  (error) => {
    // Handle response errors
    if (!error.response) {
      // Handle network errors (e.g., server down, timeout)
      console.error("Network error or server is down: ", error.message)
      return Promise.reject(error)
    }

    const { status } = error.response
    if (status === 403) {
      // Handle 403 forbidden error
      window.location.href = "/403"
    } else if (status === 401) {
      // Handle 401 unauthorized error
      if (typeof window !== "undefined" && window.location.pathname !== "/sign-in") {
        console.warn("Redirecting to login due to 401 unauthorized error")
        window.location.href = "/sign-in"
      }
    } else {
      // Handle other errors
      console.error("Request failed with status: ", status, error.message)
    }
    return Promise.reject(error)
  }
)

export default instance
