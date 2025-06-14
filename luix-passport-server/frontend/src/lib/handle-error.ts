import { z } from "zod"

export function getErrorMessage(err: unknown) {
  const unknownError = "Something went wrong, please try again later."

  if (err instanceof z.ZodError) {
    const errors = err.issues.map((issue) => {
      return issue.message
    })
    return errors.join("\n")
  } else if (err instanceof Error) {
    if("response" in err) {
      return (err as any).response.data.message;
    } else {
      return err.message
    }
  } 
  // else if (isRedirectError(err)) {
  //   throw err
  // } 
  else {
    return unknownError
  }
}
