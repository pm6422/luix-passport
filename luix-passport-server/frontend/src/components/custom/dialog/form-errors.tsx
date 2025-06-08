import React, { useState, useEffect } from "react";
import { FieldValues, UseFormReturn, FieldError, FieldErrors } from "react-hook-form";
import { IconExclamationCircle } from "@tabler/icons-react";
import { Alert, AlertTitle, AlertDescription } from "@/components/ui/alert";

interface ErrorItem {
  field: string;
  message?: string;
}

interface Props<T extends FieldValues> {
  children?: React.ReactNode;
  form: UseFormReturn<T>;
  variant?: "default" | "destructive";
  error?: FieldErrors;
}

const FormErrors = <T extends FieldValues>({
                                             children,
                                             form,
                                             variant = "destructive",
                                             error
                                           }: Props<T>) => {
  const [errors, setErrors] = useState<ErrorItem[]>([]);

  useEffect(() => {
    if (!error) return;

    const results: ErrorItem[] = [];

    Object.entries(error).forEach(([field, errorValue]) => {
      if (isFieldError(errorValue)) {
        results.push({
          field,
          message: errorValue.message
        });
      } else if (typeof errorValue === 'object' && errorValue !== null) {
        Object.entries(errorValue).forEach(([nestedField, nestedError]) => {
          if (isFieldError(nestedError)) {
            results.push({
              field: `${field}.${nestedField}`,
              message: nestedError.message
            });
          }
        });
      }
    });

    setErrors(results);
  }, [error]);

  return (
    Object.values(form.formState.errors).length > 0 && (
      <Alert variant={variant}>
        <AlertTitle className="flex items-center mb-3">
          <IconExclamationCircle className="size-5 me-1" />
          There were errors in your form, please check inputs and retry.
        </AlertTitle>
        <div className="space-y-1">
          {errors.map((err, index) => (
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
  );
};

function isFieldError(error: unknown): error is FieldError {
  return typeof error === 'object' && error !== null && 'message' in error;
}

export default FormErrors;