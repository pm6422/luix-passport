import { Card } from "@/components/ui/card"
import { Link, useSearchParams } from "react-router-dom"
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
              {success ? (
                <p className='text-muted-foreground'>
                  账号已经激活，请{' '}
                  <Link
                    to='/login'
                    className='text-primary underline-offset-4 hover:underline'
                  >
                    登录
                  </Link>
                </p>
              ) : (
                <p className='text-muted-foreground'>账号未能激活，请注册</p>
              )}
            </div>

            {!success && errorMessage && (
              <div className='mb-4 rounded bg-destructive/15 p-3 text-center text-sm text-destructive'>
                <strong>{errorMessage}</strong>
              </div>
            )}

            {!success && (
              <div className='mt-4 text-center'>
                <p className="mt-3 text-sm leading-relaxed text-gray-900">
                  <a href="/login" className="font-bold hover:underline text-blue-700">Back to Sign In</a>
                </p>
              </div>
            )}
          </Card>
        </div>
      </div>
    </>
  )
}