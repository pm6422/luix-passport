import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { PasswordInput } from "@/components/custom/password-input";
import { LoadingButton } from "@/components/custom/loading-button"
import { Link } from 'react-router-dom'

const formSchema = z.object({
  username: z.string().min(1, { message: "Please enter your username" }),
  password: z.string().min(1, { message: "Please enter your password" }),
});

export function SignInForm() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [logoutMessage, setLogoutMessage] = useState<string | null>(null);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: "",
      password: "",
    },
  });

  async function onSubmit(data: z.infer<typeof formSchema>) {
    setIsLoading(true);
    setError(null);

    try {
      const formData = new FormData();
      formData.append('username', data.username);
      formData.append('password', data.password);

      const response = await fetch('/login', {
        method: 'POST',
        body: formData,
        headers: {
          // This header is important for server-side frameworks to recognize form data
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Invalid username or password');
      }

      // Handle successful login (e.g., redirect)
      window.location.href = '/dashboard';
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Login failed');
    } finally {
      setIsLoading(false);
    }
  }

  // Check URL for the logout parameter (simulating Thymeleaf's th:if="${param.logout}")
  if (typeof window !== 'undefined') {
    const params = new URLSearchParams(window.location.search);
    if (params.get('logout')) {
      setLogoutMessage('You have been logged out');
    }
  }

  return (
    <div className="w-full">
      {/* GitHub OAuth Button */}
        <form action="/oauth2/authorization/github-idp" method="GET">
          <Button
            type="submit"
            variant="outline"
            className="w-full py-4 h-14 mb-6 rounded-2xl"
          >
            <svg
              className="h-5 w-5 mr-2"
              viewBox="0 0 2350 2314.8"
            >
              {/* GitHub logo SVG path */}
            </svg>
            Sign in with GitHub
          </Button>
        </form>

        {/* Divider */}
        <div className="flex items-center mb-6">
          <hr className="h-0 border-b border-muted grow" />
          <p className="mx-4 text-sm text-muted-foreground uppercase">or continue with</p>
          <hr className="h-0 border-b border-muted grow" />
        </div>

        {/* Error messages */}
        {error && (
          <div className="mb-6 p-4 text-center text-destructive bg-destructive/10 rounded-lg">
            {error}
          </div>
        )}
        {logoutMessage && (
          <div className="mb-6 p-4 text-center text-muted-foreground">
            {logoutMessage}
          </div>
        )}

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="yourusername"
                      className="px-5 py-4 h-14 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <div className="flex items-center justify-between">
                    <FormLabel>Password</FormLabel>
                    <Link to="/forgot-password" className="text-sm font-medium text-blue-600 hover:underline">
                      Forgot password?
                    </Link>
                  </div>
                  <FormControl>
                    <PasswordInput
                      className="px-5 py-4 h-14 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <LoadingButton type="submit" loading={isLoading} className="w-full py-4 h-14 rounded-2xl">
              {isLoading ? "Waiting..." : "Sign In"}
            </LoadingButton>
          </form>
        </Form>
    </div>
  );
}