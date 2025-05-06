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
      <div className="container grid h-svh flex-col items-center justify-center bg-primary-foreground lg:max-w-none lg:px-0">
        <div className="mx-auto flex w-full flex-col justify-center space-y-2 sm:w-[480px] lg:p-8">
          <div className="mb-4 flex items-center justify-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="mr-2 h-6 w-6"
            >
              <path d="M15 6v12a3 3 0 1 0 3-3H6a3 3 0 1 0 3 3V6a3 3 0 1 0-3 3h12a3 3 0 1 0-3-3" />
            </svg>
            <h1 className="text-xl font-medium">Shadcn Admin</h1>
          </div>
          <Card className="p-6">
            <div className="mb-6 flex flex-col space-y-2 text-center">
              <h1 className="text-4xl font-bold">激活账号</h1>
              {success ? (
                <p className="text-muted-foreground">
                  账号已经激活，请{" "}
                  <Link
                    to="/login"
                    className="text-primary underline-offset-4 hover:underline"
                  >
                    登录
                  </Link>
                </p>
              ) : (
                <p className="text-muted-foreground">账号未能激活，请注册</p>
              )}
            </div>

            {!success && errorMessage && (
              <div className="mb-4 rounded bg-destructive/15 p-3 text-center text-sm text-destructive">
                <strong>{errorMessage}</strong>
              </div>
            )}

            {!success && (
              <div className="mt-4 text-center">
                <Link
                  to="/login"
                  className="text-sm text-primary underline-offset-4 hover:underline"
                >
                  返回登录页面
                </Link>
              </div>
            )}
          </Card>
          <p className="text-center text-sm text-muted-foreground">
            <small>🅛🅞🅤🅘🅢 &copy; 2017</small>
          </p>
        </div>
      </div>
    </>
  )
}