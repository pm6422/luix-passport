import React, { useEffect, useState } from 'react'
import {
  FieldError,
  FieldErrors,
  FieldValues,
  UseFormReturn,
} from 'react-hook-form'
import { IconAlertSquareRoundedFilled } from '@tabler/icons-react'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'

interface ErrorItem {
  field: string
  message?: string
}

interface Props<T extends FieldValues> {
  children?: React.ReactNode
  form: UseFormReturn<T>
  variant?: "default" | "destructive"
  error?: FieldErrors
}

const FormErrors = <T extends FieldValues>({
  children,
  form,
  variant = "destructive",
  error,
}: Props<T>) => {
  const [errors, setErrors] = useState<ErrorItem[]>([])

  useEffect(() => {
    if (!error) return

    const results: ErrorItem[] = []

    Object.entries(error).forEach(([field, errorValue]) => {
      if (isFieldError(errorValue)) {
        results.push({
          field,
          message: errorValue.message,
        })
      } else if (typeof errorValue === 'object' && errorValue !== null) {
        Object.entries(errorValue).forEach(([nestedField, nestedError]) => {
          if (isFieldError(nestedError)) {
            results.push({
              field: `${field}.${nestedField}`,
              message: nestedError.message,
            })
          }
        })
      }
    })

    setErrors(results)
  }, [error])

  return (
    Object.values(form.formState.errors).length > 0 && (
      <Alert variant={variant} className="p-3 bg-destructive/10 border border-red-500">
        <AlertTitle className="flex items-center gap-2 text-sm">
          <IconAlertSquareRoundedFilled className="size-4" />
          Please resolve the following issues
        </AlertTitle>
        <div className="mt-2 space-y-1 text-xs">
          {errors.map((err, index) => (
            <div
              key={`${err.field}-${index}`}
              className="flex items-start gap-2"
            >
              <Badge variant="outline" className="capitalize text-[10px] px-1.5 py-0.5 leading-none">{err.field}</Badge>
              <span className="text-muted-foreground text-xs">{err.message}</span>
            </div>
          ))}
        </div>
        {children && (
          <AlertDescription className="mt-2 pr-6 text-xs">
            {children}
          </AlertDescription>
        )}
      </Alert>
    )
  )
}

const isFieldError = (error: unknown): error is FieldError => {
  return typeof error === "object" && error !== null && "message" in error
}

export default FormErrors
