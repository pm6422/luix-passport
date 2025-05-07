import { Card } from "@/components/ui/card"
import { useState } from "react"
import { cn } from "@/libs/utils"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/custom/button"
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

const formSchema = z.object({
  email: z
    .string()
    .min(1, { message: "Please enter your email" })
    .email({ message: "Invalid email address" }),
})

export default function ForgotPassword() {
  const [isLoading, setIsLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: { email: "" },
  })

  function requestReset(data: z.infer<typeof formSchema>) {
    setIsLoading(true)

    AccountService.requestPasswordRecovery(data.email)
      .then(() => {
        setSuccess(true)
        setIsLoading(false)
      })
      .catch((error) => {
        setSuccess(false)
        setIsLoading(false)
        setErrorMessage(error.response.data.message || 'Request password recovery failed')
      })
  }

  return (
    <>
      <div className="container grid h-svh flex-col items-center justify-center bg-primary-foreground lg:max-w-none lg:px-0">
        <div className="mx-auto flex w-full flex-col justify-center space-y-2 sm:w-[600px] lg:p-8">
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
                Forgot Password
              </h1>
              <p className="text-sm text-muted-foreground">
                Enter your registered email and we will send you a link
                to reset your password.
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
                <form onSubmit={form.handleSubmit(requestReset)}>
                  <div className="grid gap-2">
                    <FormField
                      control={form.control}
                      name="email"
                      render={({ field }) => (
                        <FormItem className="space-y-1">
                          <FormLabel>Email</FormLabel>
                          <FormControl>
                            <Input placeholder="name@example.com" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <Button className="mt-2" loading={isLoading}>
                      Send Reset Link
                    </Button>
                  </div>
                </form>
              </Form>
            </div>

            <p className="mt-4 px-8 text-center text-sm text-muted-foreground">
              Don't have an account?{" "}
              <a
                href="/sign-up"
                className="underline underline-offset-4 hover:text-primary"
              >
                Sign up
              </a>
              .
            </p>
          </Card>
        </div>
      </div>
    </>
  )
}