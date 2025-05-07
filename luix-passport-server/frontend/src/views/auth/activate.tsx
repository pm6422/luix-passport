import { Card } from "@/components/ui/card"
import { useSearchParams } from "react-router-dom"
import { useState } from "react"
import { AccountService } from "@/services/account-service"
import { Button } from "@/components/custom/button.tsx"
import { toast } from "sonner"
import { getErrorMessage } from "@/libs/handle-error.ts"

export default function Activate() {
  const [searchParams] = useSearchParams()
  const code = searchParams.get("code")
  const [success, setSuccess] = useState(false)

  if (!code) {
    toast.error("Invalid empty activation code")
  } else {
    toast.promise(AccountService.activate(code), {
      loading: "Activating account...",
      success: () => {
        setSuccess(true)
        return "Activated account successfully"
      },
      error: (error) => {
        setSuccess(false)
        return getErrorMessage(error)
      }
    })
  }

  return (
    <>
      <div className="container grid h-svh flex-col items-center justify-center bg-primary-foreground lg:max-w-none lg:px-0">
        <div className="mx-auto flex w-full flex-col justify-center space-y-2 sm:w-[480px] lg:p-8">
          <div className="mb-4 flex items-center justify-center">
            <img
              alt="Logo"
              src="/assets/images/logos/logo-with-text.svg"
              className="h-10"
            />
          </div>
          <Card className="p-6">
            <div className="flex flex-col space-y-5 text-center">
              <h1 className="text-3xl font-bold">Activate Account</h1>
            </div>

            <div className="mt-4 mb-6 flex flex-col space-y-5 text-center">
              {success && (
                <p className="text-muted-foreground">
                  Activation successful, please
                  <a href="/login" className="text-primary underline-offset-4 hover:underline"> login</a>
                </p>
              )}
            </div>

            {!success && (
              <div className="mt-4 text-center">
                <Button className="mt-2 w-full" >
                  <a href="/sign-up">Sign Up</a>
                </Button>
              </div>
            )}
          </Card>
        </div>
      </div>
    </>
  )
}