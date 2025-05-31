import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { PasswordInput } from "@/components/custom/password-input";
import { AccountService } from "@/services/account-service"
import { type UserRegistrationFormSchema, userRegistrationFormSchema } from "@/domains/user-registration"
import { toast } from "sonner";
import { useState } from "react"
import { getErrorMessage } from "@/lib/handle-error.ts"
import { LoadingButton } from "@/components/custom/loading-button"
import InputFormField from "@/components/custom/form-field/input"

export function SignUpForm() {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<UserRegistrationFormSchema>({
    resolver: zodResolver(userRegistrationFormSchema),
    defaultValues: {
      email: "",
      username: "",
      password: "",
      confirmPassword: "",
    },
  });

  async function onSubmit(data: UserRegistrationFormSchema) {
    setIsLoading(true)

    toast.promise(AccountService.register(data), {
      loading: "Registering account...",
      success: () => {
        setIsLoading(false)
        window.location.href = "/login";
        return "Registered account successfully"
      },
      error: (error) => {
        setIsLoading(false)
        return getErrorMessage(error)
      }
    })
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="w-full">
        <div className="flex flex-col w-full h-full px-5 text-center">
          <h3 className="mb-6 text-2xl font-bold">Create Account</h3>

          <div className="space-y-4">
            <InputFormField
              control={form.control}
              name="email"
              label="Email"
              formItemClassName="text-left"
              inputClassName="w-full px-5 py-4 rounded-2xl"
            />

            <InputFormField
              control={form.control}
              name="username"
              label="Username"
              formItemClassName="text-left"
              inputClassName="w-full px-5 py-4 rounded-2xl"
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem className="text-left">
                  <FormLabel className="mb-2 text-sm">Password</FormLabel>
                  <FormControl>
                    <PasswordInput
                      placeholder="••••••••"
                      className="w-full px-5 py-4 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="confirmPassword"
              render={({ field }) => (
                <FormItem className="text-left">
                  <FormLabel className="mb-2 text-sm">Confirm Password</FormLabel>
                  <FormControl>
                    <PasswordInput
                      placeholder="••••••••"
                      className="w-full px-5 py-4 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <LoadingButton type="submit" loading={isLoading} className="w-full px-5 py-4 mt-4 mb-2 text-sm font-medium rounded-2xl">
            {isLoading ? "Waiting..." : "Create Account"}
          </LoadingButton>

          <p className="mt-3 text-sm text-muted-foreground">
            Already have an account?{" "}
            <a href="/login" className="font-bold text-blue-600 hover:underline">
              Sign In
            </a>
          </p>
        </div>
      </form>
    </Form>
  );
}