import { useState, useEffect } from "react"
import { UseFormReturn } from "react-hook-form"
import { IconExclamationCircle } from "@tabler/icons-react"
import { Alert, AlertTitle, AlertDescription } from "@/components/ui/alert"
import { transform } from "lodash"

interface Props {
  children?: React.ReactNode
  form: UseFormReturn<any, any, any>
  variant?: "default" | "destructive"
  error?: Object
}

const FormErrors = ({
  children,
  form,
  variant = "destructive",
  error
}: Props) => {
  const [errors, setErrors] = useState<Array<any>>([])

  useEffect(() => {
    if (!error) return
    const results = transform(error, (result: any, value: any, key: any) => {
      result.push({ field: key, message: value.message })
      return { field: key, message: value.message }
    }, [])
    setErrors(results)
  }, [error])
  

  return (
    Object.values(form.formState.errors).length > 0 && (
      <Alert variant={variant}>
        <AlertTitle className="flex items-center mb-3"><IconExclamationCircle className="size-5 me-1" />There was errors in your form, please check inputs and retry.</AlertTitle>
        { errors && errors.map((err, index) => <p key={`${err.field}-${index}`} className="ms-1">- {err.field}: {err.message}</p>) }
        {children && <AlertDescription className="pr-8 font-light">{children}</AlertDescription>}
      </Alert>
    )
  )
}

export default FormErrors;
