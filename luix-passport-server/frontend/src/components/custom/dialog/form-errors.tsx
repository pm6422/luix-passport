import React, { useState, useEffect } from "react"
import { FieldValues, UseFormReturn } from 'react-hook-form'
import { IconExclamationCircle } from "@tabler/icons-react"
import { Alert, AlertTitle, AlertDescription } from "@/components/ui/alert"
import { transform } from "lodash"

interface Props<T extends FieldValues> {
  children?: React.ReactNode
  form: UseFormReturn<T>
  variant?: "default" | "destructive"
  error?: Object
}

const FormErrors = <T extends FieldValues,>({
                      children,
                      form,
                      variant = "destructive",
                      error
                    }: Props<T>) => {
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
        <AlertTitle className="flex items-center mb-3">
          <IconExclamationCircle className="size-5 me-1" />
          There were errors in your form, please check inputs and retry.
        </AlertTitle>
        <div className="space-y-1">
          {errors && errors.map((err, index) => (
            <div key={`${err.field}-${index}`} className="ms-1 whitespace-nowrap">
              - {err.field}: {err.message}
            </div>
          ))}
        </div>
        {children && (
          <AlertDescription className="pr-8 font-light mt-3">
            {children}
          </AlertDescription>
        )}
      </Alert>
    )
  )
}

export default FormErrors;