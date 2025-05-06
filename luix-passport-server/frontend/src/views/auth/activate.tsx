import { Card } from "@/components/ui/card"
import { useSearchParams } from "react-router-dom"
import { useEffect, useState } from "react"
import { AccountService } from "@/services/account-service"

export default function ActivationPage() {
  const [searchParams] = useSearchParams()
  const code = searchParams.get("code")
  const [success, setSuccess] = useState(false)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  useEffect(() => {
    if (code) {
      AccountService.activate(code)
        .then(() => {
          setSuccess(true)
        })
        .catch((error) => {
          setSuccess(false)
          setErrorMessage(error.message || "Activation failed")
        })
    } else {
      setErrorMessage("Empty activation code")
    }
  }, [code])

  return (
    <>
      <div className='container grid h-svh flex-col items-center justify-center bg-primary-foreground lg:max-w-none lg:px-0'>
        <div className='mx-auto flex w-full flex-col justify-center space-y-2 sm:w-[480px] lg:p-8'>
          <div className='mb-4 flex items-center justify-center'>
            <img
              alt='Logo'
              src='/assets/images/logos/logo-with-text.svg'
              className='h-10'
            />
          </div>
          <Card className='p-6'>
            <div className='mb-6 flex flex-col space-y-5 text-center'>
              <h1 className='text-3xl font-bold'>Activate Account</h1>
            </div>

            {!success && errorMessage && (
              <div className='mb-4 rounded p-3 text-center text-sm text-destructive'>
                <strong>{errorMessage}</strong>
              </div>
            )}

            <div className='mb-6 flex flex-col space-y-5 text-center'>
              {success ? (
                <p className='text-muted-foreground'>
                  Account activation successful, please
                  <a href="/login" className="text-primary underline-offset-4 hover:underline"> login</a>
                </p>
              ) : (
                <p className='font-bold'>Activation failed</p>
              )}
            </div>

            {!success && (
              <div className='mt-4 text-center'>
                <p className="mt-3 text-sm leading-relaxed text-gray-900">
                  <a href="/sign-up" className="font-bold hover:underline text-blue-700">Back to Sign Up</a>
                </p>
              </div>
            )}
          </Card>
        </div>
      </div>
    </>
  )
}