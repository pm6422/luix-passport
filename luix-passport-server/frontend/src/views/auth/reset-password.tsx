import { Card } from "@/components/ui/card"
import { useState } from "react"
import { cn } from "@/lib/utils"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from 'zod'
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
import { toast } from "sonner"
import { Input } from "@/components/ui/input"
import { AccountService } from "@/services/account-service"
import { useSearchParams } from "react-router-dom"
import { PasswordInput } from '@/components/custom/password-input.tsx'
import { getErrorMessage } from '@/lib/handle-error'

const formSchema = z
  .object({
    newRawPassword: z
      .string()
      .min(5, { message: "Password must be at least 5 characters", })
      .max(10, { message: "Password must be at most 10 characters", }),
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
  const [searchParams] = useSearchParams()
  const resetCode = searchParams.get('resetCode')

  if (!resetCode) {
    toast.error("Invalid empty reset code")
  }

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      newRawPassword: "",
      confirmPassword: "",
      resetCode: resetCode || ""
    }
  })

  function completeReset(data: z.infer<typeof formSchema>) {
    setIsLoading(true)

    toast.promise(AccountService.completePasswordRecovery(data), {
      loading: "Resetting password...",
      success: () => {
        setIsLoading(false)
        setTimeout(() => {
          // navigate to login
          navigate("/")
        }, 2000)
        return "Reset password successfully"
      },
      error: (error) => {
        setIsLoading(false)
        return getErrorMessage(error)
      }
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