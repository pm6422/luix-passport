import { z } from "zod"
import type { AxiosError } from "axios"

export function getErrorMessage(err: unknown) {
  const unknownError = "Something went wrong, please try again later."

  if (err instanceof z.ZodError) {
    const errors = err.issues.map((issue) => {
      return issue.message
    })
    return errors.join("\n")
  } else if (err instanceof Error) {
    const axiosErr = err as AxiosError<{ message?: string }>
    if (axiosErr.response?.data) {
      return axiosErr.response.data.message ?? err.message
    }
    return err.message
  } 
  // else if (isRedirectError(err)) {
  //   throw err
  // } 
  else {
    return unknownError
  }
}
