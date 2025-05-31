import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { IconBook, IconCode, IconKey, IconApi } from "@tabler/icons-react";

export default function ApiReferencePage() {
  const docSections = [
    {
      title: "Installation",
      description: "Set up the SDK in your project.",
      link: "/docs/installation",
      icon: <IconCode className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "Authentication",
      description: "Implement secure authentication flows.",
      link: "/docs/authentication",
      icon: <IconKey className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "API Reference",
      description: "Explore our API endpoints.",
      link: "/docs/api-reference",
      icon: <IconBook className="h-5 w-5 text-primary mr-2" />,
    },
  ];

  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-3">
      {/* Left side - 1/3 width */}
      <div className="lg:flex relative hidden lg:col-span-1 lg:px-12 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-4xl text-3xl font-extrabold lg:leading-[50px]">
            Documentation
          </h2>
          <h4 className="mt-6 text-lg">
            Everything you need to build with our platform.
          </h4>
          <div className="mt-10">
            <Button
              asChild
              variant="outline"
              className="px-8 py-4 h-14 text-base rounded-2xl"
            >
              <Link to="/sign-up">Get Started</Link>
            </Button>
          </div>
          <nav className="mt-10">
            <ul className="space-y-4">
              {docSections.map((section, index) => (
                <li key={index}>
                  <Link
                    to={section.link}
                    className={`flex items-center text-base ${
                      section.title === "API Reference"
                        ? "text-primary font-semibold"
                        : "text-muted-foreground hover:text-primary"
                    }`}
                  >
                    {section.icon}
                    {section.title}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>
        </div>
      </div>

      {/* Right side - 2/3 width */}
      <div className="flex flex-col w-full h-full lg:col-span-2 xl:px-32 lg:px-20 px-5 rounded-3xl py-10 overflow-y-auto">
        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2">API Reference</h1>
          <p className="text-lg text-muted-foreground mb-10">
            Explore our API endpoints to manage users, authentication, and data.
          </p>

          <div className="space-y-12">
            {/* User Management */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconApi className="h-5 w-5 text-primary mr-2" />
                User Management
              </h3>
              <p className="text-muted-foreground mb-4">
                Manage user accounts and profiles.
              </p>
              <h4 className="font-medium mb-2">GET /api/users/{'{userId}'}</h4>
              <p className="text-muted-foreground mb-2">Retrieve user details.</p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`curl -X GET "https://api.luix-universe.com/v1/users/{userId}" \\
-H "Authorization: Bearer {your-api-key}"
`}
                </pre>
              </div>
              <p className="text-muted-foreground mt-4 mb-2">Response:</p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`{
  "id": "{userId}",
  "email": "user@example.com",
  "name": "John Doe",
  "createdAt": "2025-05-31T18:31:00Z"
}
`}
                </pre>
              </div>
            </div>

            {/* Authentication */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconApi className="h-5 w-5 text-primary mr-2" />
                Authentication
              </h3>
              <p className="text-muted-foreground mb-4">
                Authenticate users and obtain access tokens.
              </p>
              <h4 className="font-medium mb-2">POST /api/auth/login</h4>
              <p className="text-muted-foreground mb-2">Log in a user.</p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`curl -X POST "https://api.luix-universe.com/v1/auth/login" \\
-H "Content-Type: application/json" \\
-d '{"email": "user@example.com", "password": "your-password"}'
`}
                </pre>
              </div>
              <p className="text-muted-foreground mt-4 mb-2">Response:</p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
`}
                </pre>
              </div>
            </div>

            {/* Data Retrieval */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconApi className="h-5 w-5 text-primary mr-2" />
                Data Retrieval
              </h3>
              <p className="text-muted-foreground mb-4">
                Fetch data from your application.
              </p>
              <h4 className="font-medium mb-2">GET /api/data</h4>
              <p className="text-muted-foreground mb-2">Retrieve data entries.</p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`curl -X GET "https://api.luix-universe.com/v1/data?limit=10" \\
-H "Authorization: Bearer {your-api-key}"
`}
                </pre>
              </div>
              <p className="text-muted-foreground mt-4 mb-2">Response:</p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`[
  {
    "id": "data1",
    "value": "Sample Data",
    "createdAt": "2025-05-31T18:31:00Z"
  },
  ...
]
`}
                </pre>
              </div>
            </div>
          </div>

          <div className="mt-16 border rounded-2xl p-6 bg-muted/50">
            <h3 className="text-xl font-semibold mb-4">Need Help?</h3>
            <div className="space-y-6">
              <div>
                <h4 className="font-medium">Community Support</h4>
                <p className="text-muted-foreground mt-1">
                  Join our developer community on Discord or GitHub to ask questions and share ideas.
                </p>
              </div>
              <div>
                <h4 className="font-medium">Enterprise Support</h4>
                <p className="text-muted-foreground mt-1">
                  Get dedicated support with our Enterprise plan, including a personal account manager.
                </p>
              </div>
              <div>
                <h4 className="font-medium">API Documentation</h4>
                <p className="text-muted-foreground mt-1">
                  Access our full API documentation for detailed endpoint information.
                </p>
                <Button
                  asChild
                  variant="outline"
                  className="mt-2 px-4 py-2 h-10 text-sm rounded-2xl"
                >
                  <Link to="/docs/api-full">View Full Docs</Link>
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}