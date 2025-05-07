import { Card } from "@/components/ui/card"
import { useState } from "react"
import { cn } from "@/libs/utils"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/custom/button"
import { useNavigate } from "react-router-dom"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { AccountService } from "@/services/account-service"
import { useSearchParams } from "react-router-dom"
import { PasswordInput } from '@/components/custom/password-input.tsx'

const formSchema = z
  .object({
    newRawPassword: z.string().min(5, { message: "Please enter your new password with at least 5 characters", }).max(10, { message: "Password must be at least 10 characters long", }),
    confirmPassword: z.string(),
    resetCode: z.string().min(1, { message: "Please enter your reset code" }),
  })
  .refine((data) => data.newRawPassword === data.confirmPassword, {
    message: "Passwords don't match.",
    path: ["confirmPassword"],
  })

export default function ForgotPassword() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [searchParams] = useSearchParams()

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      newRawPassword: "",
      confirmPassword: "",
      resetCode:  searchParams.get('resetCode') || ""
    }
  })

  function completeReset(data: z.infer<typeof formSchema>) {
    setIsLoading(true)

    AccountService.completePasswordRecovery(data)
      .then(() => {
        setSuccess(true)
        setIsLoading(false)
        // navigate to login
        navigate("/")
      })
      .catch((error) => {
        setSuccess(false)
        setIsLoading(false)
        setErrorMessage(error.response.data.message || 'Reset password failed')
      })
  }

  return (
    <>
      <div className="container grid h-svh flex-col items-center justify-center bg-primary-foreground lg:max-w-none lg:px-0">
        <div className="mx-auto flex w-full flex-col justify-center space-y-2 sm:w-[480px] lg:p-8">
          <div className='mb-4 flex items-center justify-center'>
            <img
              alt='Logo'
              src='/assets/images/logos/logo-with-text.svg'
              className='h-10'
            />
          </div>
          <Card className="p-6">
            <div className="mb-2 flex flex-col space-y-2 text-left">
              <h1 className="text-md font-semibold tracking-tight">
                Reset Password
              </h1>
              <p className="text-sm text-muted-foreground">
                Enter your new password to reset.
              </p>
            </div>

            {!success && errorMessage && (
              <div>
                <div className='text-center text-xs text-destructive'>
                  <strong>{errorMessage}</strong>
                </div>
              </div>
            )}

            {/* Former ForgotForm content now directly integrated */}
            <div className={cn("grid gap-6")}>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(completeReset)}>
                  <div className="grid gap-2">
                    <FormField
                      control={form.control}
                      name="resetCode"
                      render={({ field }) => (
                        <FormItem className="space-y-1">
                          <FormLabel>Reset Code</FormLabel>
                          <FormControl>
                            <Input disabled {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="newRawPassword"
                      render={({ field }) => (
                        <FormItem className="space-y-1">
                          <FormLabel>New Password</FormLabel>
                          <FormControl>
                            <PasswordInput {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="confirmPassword"
                      render={({ field }) => (
                        <FormItem className="space-y-1">
                          <FormLabel>Confirm New Password</FormLabel>
                          <FormControl>
                            <PasswordInput {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <Button className="mt-2" loading={isLoading}>
                      Reset
                    </Button>
                  </div>
                </form>
              </Form>
            </div>
          </Card>
        </div>
      </div>
    </>
  )
}